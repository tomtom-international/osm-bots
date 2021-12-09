package osm.bots.rings.inner.duplicates.verifiers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InnerRingWayIsOnlyMemberOfViolatingRelationVerifierTest {

    private InnerRingWayIsOnlyMemberOfViolatingRelationVerifier innerRingWayIsOnlyMemberOfViolatingRelationVerifier;

    public static Stream<Arguments> getTestCases() {
        return Stream.of(
                TestCaseGenerator.testCase()
                        .name("Inner ring way has no other parent relations")
                        .violatingRelationId(1L)
                        .innerRingWayParentRelations(1L)
                        .verifierResult(true),
                TestCaseGenerator.testCase()
                        .name("Inner ring way has no parent relations")
                        .violatingRelationId(1L)
                        .innerRingWayParentRelations(List.of())
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Inner ring way has more parent relations")
                        .violatingRelationId(1L)
                        .innerRingWayParentRelations(List.of(1L, 2L, 3L))
                        .verifierResult(false))
                .map(TestCaseGenerator.TestCaseBuilder::build);
    }

    @BeforeEach
    void setUp() {
        this.innerRingWayIsOnlyMemberOfViolatingRelationVerifier = new InnerRingWayIsOnlyMemberOfViolatingRelationVerifier();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldVerifyThatWayFromViolatingRelationHasNoOtherParentRelations(
            String testCaseName,
            ViolatingOsmData violatingOsmData,
            boolean verifierResult) {

        assertThat(innerRingWayIsOnlyMemberOfViolatingRelationVerifier.test(violatingOsmData))
                .isEqualTo(verifierResult);
    }
}
