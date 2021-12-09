package osm.bots.rings.inner.duplicates.verifiers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InnerRingWayHasNoTagsVerifierTest {

    private InnerRingWayHasNoTagsVerifier innerRingWayHasNoTagsVerifier;

    public static Stream<Arguments> getTestCases() {
        return Stream.of(
                TestCaseGenerator.testCase()
                        .name("Inner ring way with tags should not pass")
                        .innerRingWayTags("key", "value")
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Inner ring way without tags should pass")
                        .innerRingWayTags(Map.of())
                        .verifierResult(true)
        )

                .map(TestCaseGenerator.TestCaseBuilder::build);
    }

    @BeforeEach
    void setUp() {
        this.innerRingWayHasNoTagsVerifier = new InnerRingWayHasNoTagsVerifier();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldVerifyIfWayInViolatingRelationHasNoTags(
            String testCaseName,
            ViolatingOsmData violatingOsmData,
            boolean verifierResult) {

        assertThat(innerRingWayHasNoTagsVerifier.test(violatingOsmData))
                .isEqualTo(verifierResult);
    }
}
