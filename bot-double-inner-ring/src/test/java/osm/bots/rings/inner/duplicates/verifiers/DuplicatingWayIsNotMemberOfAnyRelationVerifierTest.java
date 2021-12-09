package osm.bots.rings.inner.duplicates.verifiers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicatingWayIsNotMemberOfAnyRelationVerifierTest {

    private DuplicatingWayIsNotMemberOfAnyRelationVerifier duplicatingWayIsNotMemberOfAnyRelationVerifier;

    public static Stream<Arguments> getTestCases() {
        return Stream.of(
                TestCaseGenerator.testCase()
                        .name("Duplicating way that does not belong to any relation should pass verification")
                        .duplicatingWayParentRelations(List.of())
                        .verifierResult(true),
                TestCaseGenerator.testCase()
                        .name("Duplicating way that belongs to violating relation should not pass verification")
                        .violatingRelationId(1L)
                        .duplicatingWayParentRelations(1L)
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Duplicating way that belongs to different relation than violating one should not pass verification")
                        .violatingRelationId(1L)
                        .duplicatingWayParentRelations(2L)
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Duplicating way that belongs to different relations than violating one should not pass verification")
                        .violatingRelationId(1L)
                        .duplicatingWayParentRelations(List.of(2L, 3L, 4L))
                        .verifierResult(false))
                .map(TestCaseGenerator.TestCaseBuilder::build);
    }

    @BeforeEach
    void setUp() {
        this.duplicatingWayIsNotMemberOfAnyRelationVerifier = new DuplicatingWayIsNotMemberOfAnyRelationVerifier();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldVerifyThatOtherWayHasNoParentRelations(
            String description,
            ViolatingOsmData violatingOsmData,
            boolean verifierResult) {

        assertThat(duplicatingWayIsNotMemberOfAnyRelationVerifier.test(violatingOsmData))
                .isEqualTo(verifierResult);
    }
}
