package osm.bots.rings.inner.duplicates.verifiers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

final class DuplicatingWayIsNotRevertedCoastLineVerifierTest {

    private static final List<Long> CLOSED_RING = List.of(1L, 2L, 3L, 4L, 1L);
    private static final List<Long> CLOSED_RING_REVERTED = List.of(1L, 4L, 3L, 2L, 1L);

    public static Stream<Arguments> getTestCases() {
        return Stream.of(
                TestCaseGenerator.testCase()
                        .name("Duplicated way without any tags is not coastline")
                        .innerRingWayNodes(CLOSED_RING)
                        .duplicatingWayNodes(CLOSED_RING)
                        .verifierResult(true),
                TestCaseGenerator.testCase()
                        .name("Duplicated way with random tag is not coastline")
                        .innerRingWayNodes(CLOSED_RING)
                        .duplicatingWayNodes(CLOSED_RING)
                        .duplicatingWayTags(Map.of("random", "random"))
                        .verifierResult(true),
                TestCaseGenerator.testCase()
                        .name("Duplicated way with tag natural=scrub is not coastline")
                        .innerRingWayNodes(CLOSED_RING)
                        .duplicatingWayNodes(CLOSED_RING)
                        .duplicatingWayTags(Map.of("natural", "scrub"))
                        .verifierResult(true),
                TestCaseGenerator.testCase()
                        .name("Duplicated way with tag natural=coastline and same geometry as inner ring way is coastline")
                        .innerRingWayNodes(CLOSED_RING)
                        .duplicatingWayNodes(CLOSED_RING)
                        .duplicatingWayTags(Map.of("natural", "coastline"))
                        .verifierResult(true),
                TestCaseGenerator.testCase()
                        .name("Duplicated way with tag natural=coastline but with other geometry than inner ring way is not coastline")
                        .innerRingWayNodes(CLOSED_RING_REVERTED)
                        .duplicatingWayNodes(CLOSED_RING)
                        .duplicatingWayTags(Map.of("natural", "coastline"))
                        .verifierResult(false))
                .map(TestCaseGenerator.TestCaseBuilder::build);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldVerifyCoastLine(
            String description,
            ViolatingOsmData violatingOsmData,
            boolean verifierResult) {
        // Given
        DuplicatingWayIsNotRevertedCoastLineVerifier duplicatingWayIsNotRevertedCoastLineVerifier = new DuplicatingWayIsNotRevertedCoastLineVerifier();

        // When
        boolean verificationResults = duplicatingWayIsNotRevertedCoastLineVerifier.isMatchingVerifierCriteria(violatingOsmData);

        // Then
        assertThat(verificationResults).isEqualTo(verifierResult);
    }
}
