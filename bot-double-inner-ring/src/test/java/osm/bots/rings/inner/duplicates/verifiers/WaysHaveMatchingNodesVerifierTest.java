package osm.bots.rings.inner.duplicates.verifiers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WaysHaveMatchingNodesVerifierTest {

    private WaysHaveMatchingNodesVerifier waysHaveMatchingNodesVerifier;

    public static Stream<Arguments> getTestCases() {
        return Stream.of(
                TestCaseGenerator.testCase()
                        .name("Two closed ways have the same nodes in the same order")
                        .innerRingWayNodes(List.of(1L, 2L, 3L, 4L, 1L))
                        .duplicatingWayNodes(List.of(1L, 2L, 3L, 4L, 1L))
                        .verifierResult(true),
                TestCaseGenerator.testCase()
                        .name("Two closed ways have the same nodes with shifted order")
                        .innerRingWayNodes(List.of(1L, 2L, 3L, 4L, 1L))
                        .duplicatingWayNodes(List.of(3L, 4L, 1L, 2L, 3L))
                        .verifierResult(true),
                TestCaseGenerator.testCase()
                        .name("Two closed ways have different nodes")
                        .innerRingWayNodes(List.of(1L, 2L, 3L, 4L, 1L))
                        .duplicatingWayNodes(List.of(5L, 6L, 7L, 8L, 5L))
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("One way has missing node")
                        .innerRingWayNodes(List.of(1L, 2L, 3L, 4L, 1L))
                        .duplicatingWayNodes(List.of(1L, 2L, 4L, 1L))
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("One way has additional node")
                        .innerRingWayNodes(List.of(1L, 2L, 3L, 4L, 1L))
                        .duplicatingWayNodes(List.of(1L, 2L, 3L, 4L, 5L, 1L))
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Two not closed ways have the same nodes in the same order")
                        .innerRingWayNodes(List.of(1L, 2L, 3L, 4L))
                        .duplicatingWayNodes(List.of(1L, 2L, 3L, 4L))
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Closed and not closed ways have the same nodes in the same order")
                        .innerRingWayNodes(List.of(1L, 2L, 3L, 4L))
                        .duplicatingWayNodes(List.of(1L, 2L, 3L, 4L, 1L))
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Not closed and closed ways have the same nodes in the same order")
                        .innerRingWayNodes(List.of(1L, 2L, 3L, 4L, 1L))
                        .duplicatingWayNodes(List.of(1L, 2L, 3L, 4L))
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Two not closed ways have the same nodes in different order")
                        .innerRingWayNodes(List.of(1L, 2L, 3L, 4L))
                        .duplicatingWayNodes(List.of(2L, 3L, 4L, 1L))
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Two ways have not enough nodes to form closed ring - no nodes")
                        .innerRingWayNodes(List.of())
                        .duplicatingWayNodes(List.of())
                        .verifierResult(false),
                TestCaseGenerator.testCase()
                        .name("Two ways have not enough nodes to form closed ring - only 3 nodes")
                        .innerRingWayNodes(List.of(1L, 2L, 1L))
                        .duplicatingWayNodes(List.of(1L, 2L, 1L))
                        .verifierResult(false)
        )
                .map(TestCaseGenerator.TestCaseBuilder::build);
    }

    @BeforeEach
    void setUp() {
        this.waysHaveMatchingNodesVerifier = new WaysHaveMatchingNodesVerifier();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldVerifyIfWaysHaveTheSameNodes(
            String description,
            ViolatingOsmData violatingOsmData,
            boolean verifierResult) {
        assertThat(waysHaveMatchingNodesVerifier.test(violatingOsmData))
                .isEqualTo(verifierResult);
    }
}
