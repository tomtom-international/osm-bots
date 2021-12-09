package osm.bots.rings.inner.duplicates.verifiers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicatingWayHasTagsVerifierTest {

    private DuplicatingWayHasTagsVerifier duplicatingWayHasTagsVerifier;

    public static Stream<Arguments> getTestCases() {
        return Stream.of(
                TestCaseGenerator.testCase()
                        .name("Way with tags which does not belong to relation should pass verification")
                        .duplicatingWayTags("key", "value")
                        .verifierResult(true),
                TestCaseGenerator.testCase()
                        .name("Way without tags which does not belong to relation should pass verification")
                        .duplicatingWayTags(Map.of())
                        .verifierResult(false)
        )
                .map(TestCaseGenerator.TestCaseBuilder::build);
    }

    @BeforeEach
    void setUp() {
        this.duplicatingWayHasTagsVerifier = new DuplicatingWayHasTagsVerifier();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldVerifyIfWayNotInViolatingRelationHasAnyTags(
            String description,
            ViolatingOsmData violatingOsmData,
            boolean verifierResult) {

        assertThat(duplicatingWayHasTagsVerifier.test(violatingOsmData))
                .isEqualTo(verifierResult);
    }
}
