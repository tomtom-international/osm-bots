package osm.bots.rings.inner.duplicates.osmose;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicatedViolationFilterTest {

    private DuplicatedViolationFilter duplicatedViolationsFilter;

    public static Stream<Arguments> getTestCases() {

        return Stream.of(
                testCase()
                        .description("Two different violations should not be filtered out")
                        .violation(duplicatedInnerPolygonViolation()
                                .wayId(1L)
                                .wayId(2L))
                        .violation(duplicatedInnerPolygonViolation()
                                .wayId(3L)
                                .wayId(4L))
                        .expectedViolation(duplicatedInnerPolygonViolation()
                                .wayId(1L)
                                .wayId(2L))
                        .expectedViolation(duplicatedInnerPolygonViolation()
                                .wayId(3L)
                                .wayId(4L)),
                testCase()
                        .description("Two violations with the same way IDs should be filtered out")
                        .violation(duplicatedInnerPolygonViolation()
                                .wayId(1L)
                                .wayId(2L))
                        .violation(duplicatedInnerPolygonViolation()
                                .wayId(1L)
                                .wayId(2L)),
                testCase()
                        .description("Two violations with one the same way ID should be filtered out")
                        .violation(duplicatedInnerPolygonViolation()
                                .wayId(1L)
                                .wayId(2L))
                        .violation(duplicatedInnerPolygonViolation()
                                .wayId(3L)
                                .wayId(2L)))
                .map(ArgumentsBuilder::build);
    }

    @Builder(builderMethodName = "testCase")
    private static Arguments buildTestCase(
            @NonNull String description,
            @Singular List<DuplicatedInnerPolygonViolationBuilder> violations,
            @Singular List<DuplicatedInnerPolygonViolationBuilder> expectedViolations) {
        return Arguments.of(description, buildViolations(violations), buildViolations(expectedViolations));
    }

    private static List<DuplicatedInnerPolygonViolation> buildViolations(List<DuplicatedInnerPolygonViolationBuilder> violations) {
        return violations
                .stream()
                .map(DuplicatedInnerPolygonViolationBuilder::build)
                .collect(Collectors.toList());
    }

    @Builder(builderMethodName = "duplicatedInnerPolygonViolation", builderClassName = "DuplicatedInnerPolygonViolationBuilder")
    private static DuplicatedInnerPolygonViolation buildDuplicatedInnerPolygonViolation(
            int osmoseRuleId,
            @Singular List<Long> relationIds,
            @Singular List<Long> wayIds) {
        return new DuplicatedInnerPolygonViolation(osmoseRuleId, new ViolatingOsmIds(relationIds, wayIds));
    }

    @BeforeEach
    void setUp() {
        duplicatedViolationsFilter = new DuplicatedViolationFilter();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldDeduplicateViolations(String description, List<DuplicatedInnerPolygonViolation> violations, List<DuplicatedInnerPolygonViolation> expected) {
        //  when
        List<DuplicatedInnerPolygonViolation> actual = duplicatedViolationsFilter.deduplicate(violations);

        //  then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
