package osm.bots.rings.inner.duplicates.verifiers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SourceVerifierTest {

    private static final String SOURCE_1 = "CanVec 4.0 - NRCan";
    private static final String SOURCE_2 = "CanVec 10.0 - NRCan";
    private static final String SOURCE_3 = "CanVec 11.0 - NRCan";
    private static final Map<String, String> SOURCE_TAG_1 = Map.of("source", SOURCE_1);
    private static final Map<String, String> SOURCE_TAG_2 = Map.of("source", SOURCE_2);
    private static final Map<String, String> SOURCE_TAG_3 = Map.of("source", SOURCE_3);

    public static Stream<Arguments> getTestCases() {
        return Stream.of(
                TestCaseGenerator.sourceVerifierTestCase()
                        .name("Should pass for the same source matching config")
                        .sourceTagValues(SOURCE_1)
                        .relationTags(SOURCE_TAG_1)
                        .duplicatingWayTags(SOURCE_TAG_1)
                        .verifierResult(true),
                TestCaseGenerator.sourceVerifierTestCase()
                        .name("Should not pass for different way source tag.")
                        .sourceTagValues(SOURCE_1)
                        .relationTags(SOURCE_TAG_1)
                        .duplicatingWayTags(SOURCE_TAG_2)
                        .verifierResult(false),
                TestCaseGenerator.sourceVerifierTestCase()
                        .name("Should not pass for different way and relation source tags.")
                        .sourceTagValues(SOURCE_2)
                        .relationTags(SOURCE_TAG_1)
                        .duplicatingWayTags(SOURCE_TAG_1)
                        .verifierResult(false),
                TestCaseGenerator.sourceVerifierTestCase()
                        .name("Should not pass for different relation source tag.")
                        .sourceTagValues(SOURCE_1)
                        .relationTags(SOURCE_TAG_2)
                        .duplicatingWayTags(SOURCE_TAG_1)
                        .verifierResult(false),
                TestCaseGenerator.sourceVerifierTestCase()
                        .name("2 Source tag values in config, one of them is present on relation and way, verifier should pass")
                        .sourceTagValues(List.of(SOURCE_1, SOURCE_2))
                        .relationTags(SOURCE_TAG_2)
                        .duplicatingWayTags(SOURCE_TAG_2)
                        .verifierResult(true),
                TestCaseGenerator.sourceVerifierTestCase()
                        .name("2 Source tag values in config, relation has one of them, way has another one, verifier should pass")
                        .sourceTagValues(List.of(SOURCE_1, SOURCE_2))
                        .relationTags(SOURCE_TAG_2)
                        .duplicatingWayTags(SOURCE_TAG_1)
                        .verifierResult(true),
                TestCaseGenerator.sourceVerifierTestCase()
                        .name("2 Source tag values in config, relation and way have same, not matching to config tag, verifier should fail")
                        .sourceTagValues(List.of(SOURCE_1, SOURCE_2))
                        .relationTags(SOURCE_TAG_3)
                        .duplicatingWayTags(SOURCE_TAG_3)
                        .verifierResult(false)
        )
                .map(TestCaseGenerator.SourceVerifierTestCaseBuilder::build);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldVerifyIfSourceTagsAreTheSameOnRelationAndInnerWay(
            String description,
            List<String> sourceTagValues,
            ViolatingOsmData violatingOsmData,
            boolean verifierResult) {
        //  given
        SourceVerifier sourceVerifier = new SourceVerifier(sourceTagValues);

        //  when
        boolean verificationResults = sourceVerifier.isMatchingVerifierCriteria(violatingOsmData);

        //  then
        assertThat(verificationResults).isEqualTo(verifierResult);
    }
}
