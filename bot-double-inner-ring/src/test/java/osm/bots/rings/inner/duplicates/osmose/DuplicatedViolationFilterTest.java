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

    public static Stream<Arguments> getTestCasesForUniqueViolations() {
        return Stream.of(
                        testCaseForUniqueViolations()
                                .description("Two different violations should not be filtered out")
                                .violation(osmoseViolation()
                                        .wayId(1L).wayId(2L))
                                .violation(osmoseViolation()
                                        .wayId(3L).wayId(4L))
                                .expectedViolation(osmoseViolation()
                                        .wayId(1L).wayId(2L))
                                .expectedViolation(osmoseViolation()
                                        .wayId(3L).wayId(4L)),

                        testCaseForUniqueViolations()
                                .description("Two violations with repeating way IDs should be filtered out")
                                .violation(osmoseViolation()
                                        .wayId(1L).wayId(2L))
                                .violation(osmoseViolation()
                                        .wayId(1L).wayId(2L)),

                        testCaseForUniqueViolations()
                                .description("Two violations with one repeating way ID should be filtered out")
                                .violation(osmoseViolation()
                                        .wayId(1L).wayId(2L))
                                .violation(osmoseViolation()
                                        .wayId(3L).wayId(2L)))
                .map(TestCaseForUniqueViolations::build);
    }

    public static Stream<Arguments> getTestCasesForDuplicatedViolations() {
        return Stream.of(
                        testCaseForDuplicatedViolations()
                                .description("Three violations with repeating way ids should be returned")
                                .violation(osmoseViolation()
                                        .wayId(1L).wayId(3L).relationId(1L))
                                .violation(osmoseViolation()
                                        .wayId(1L).wayId(4L).relationId(2L))
                                .violation(osmoseViolation()
                                        .wayId(1L).wayId(5L).relationId(3L))
                                .expectedViolation(new DuplicatedViolation(List.of(
                                        osmoseViolation()
                                                .wayId(1L).wayId(3L).relationId(1L).build(),
                                        osmoseViolation()
                                                .wayId(1L).wayId(4L).relationId(2L).build(),
                                        osmoseViolation()
                                                .wayId(1L).wayId(5L).relationId(3L).build()
                                ))),

                        testCaseForDuplicatedViolations()
                                .description("Two violations with not repeating way ids should not be returned")
                                .violation(osmoseViolation()
                                        .wayId(1L).wayId(2L).relationId(1L))
                                .violation(osmoseViolation()
                                        .wayId(3L).wayId(4L).relationId(2L)),

                        testCaseForDuplicatedViolations()
                                .description("Two violations with repeating way ids and relation id should not be returned")
                                .violation(osmoseViolation()
                                        .wayId(2L).wayId(3L).relationId(1L))
                                .violation(osmoseViolation()
                                        .wayId(2L).wayId(4L).relationId(1L)),

                        testCaseForDuplicatedViolations()
                                .description("Two violations with repeating way id should be returned")
                                .violation(osmoseViolation()
                                        .wayId(2L).wayId(3L).relationId(1L))
                                .violation(osmoseViolation()
                                        .wayId(2L).wayId(4L).relationId(2L))
                                .expectedViolation(new DuplicatedViolation(List.of(
                                        osmoseViolation()
                                                .wayId(2L).wayId(3L).relationId(1L).build(),
                                        osmoseViolation()
                                                .wayId(2L).wayId(4L).relationId(2L).build()
                                ))))
                .map(TestCaseForDuplicatedViolations::build);
    }

    @Builder(builderMethodName = "testCaseForUniqueViolations", builderClassName = "TestCaseForUniqueViolations")
    private static Arguments buildTestCaseForUniqueViolations(
            @NonNull String description,
            @Singular List<OsmoseViolationBuilder> violations,
            @Singular List<OsmoseViolationBuilder> expectedViolations) {
        return Arguments.of(description, buildViolations(violations), buildViolations(expectedViolations));
    }

    @Builder(builderMethodName = "testCaseForDuplicatedViolations", builderClassName = "TestCaseForDuplicatedViolations")
    private static Arguments buildTestCaseForDuplicatedViolations(
            @NonNull String description,
            @Singular List<OsmoseViolationBuilder> violations,
            @Singular List<DuplicatedViolation> expectedViolations) {
        return Arguments.of(description, buildViolations(violations), expectedViolations);
    }

    private static List<InnerPolygonOsmoseViolation> buildViolations(List<OsmoseViolationBuilder> violations) {
        return violations
                .stream()
                .map(OsmoseViolationBuilder::build)
                .collect(Collectors.toList());
    }

    @Builder(builderMethodName = "osmoseViolation", builderClassName = "OsmoseViolationBuilder")
    private static InnerPolygonOsmoseViolation buildInnerPolygonOsmoseViolation(
            int osmoseRuleId,
            @Singular List<Long> relationIds,
            @Singular List<Long> wayIds) {
        return new InnerPolygonOsmoseViolation(osmoseRuleId, new ViolatingOsmIds(relationIds, wayIds));
    }

    @BeforeEach
    void setUp() {
        duplicatedViolationsFilter = new DuplicatedViolationFilter();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCasesForUniqueViolations")
    void shouldFindUniqueViolations(String description, List<InnerPolygonOsmoseViolation> violations, List<InnerPolygonOsmoseViolation> expected) {
        //  when
        List<InnerPolygonOsmoseViolation> actual = duplicatedViolationsFilter.findUniqueViolations(violations);

        //  then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCasesForDuplicatedViolations")
    void shouldFindDuplicatedViolations(String description, List<InnerPolygonOsmoseViolation> violations, List<DuplicatedViolation> expected) {
        //  when
        List<DuplicatedViolation> actual = duplicatedViolationsFilter.findDuplicatedViolations(violations);

        //  then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
