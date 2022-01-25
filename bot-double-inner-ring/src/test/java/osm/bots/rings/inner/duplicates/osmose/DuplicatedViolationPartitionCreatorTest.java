package osm.bots.rings.inner.duplicates.osmose;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.fix.Partitions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DuplicatedViolationPartitionCreatorTest {

    private static final int EXAMPLE_RULE_ID = 1;
    private DuplicatedViolationPartitionCreator partitionCreator;

    private static Stream<Arguments> getTestCases() {
        return Stream.of(
                testCase()
                        .description("Should create 1 partition")
                        .partitionSize(10)
                        .duplicatedViolationWithRelationIds(List.of(1L, 2L, 3L))
                        .duplicatedViolationWithRelationIds(List.of(4L, 5L))
                        .expectedPartitionWithRelationIds(List.of(1L, 2L, 3L, 4L, 5L)),
                testCase()
                        .description("Should create 2 partitions due to partition size limit")
                        .partitionSize(3)
                        .duplicatedViolationWithRelationIds(List.of(1L, 2L, 3L))
                        .duplicatedViolationWithRelationIds(List.of(4L, 5L))
                        .expectedPartitionWithRelationIds(List.of(1L, 2L, 3L))
                        .expectedPartitionWithRelationIds(List.of(4L, 5L)),
                testCase()
                        .description("Should create 3 partitions due to repeating relation id")
                        .partitionSize(10)
                        .duplicatedViolationWithRelationIds(List.of(1L, 2L, 3L))
                        .duplicatedViolationWithRelationIds(List.of(1L, 4L))
                        .duplicatedViolationWithRelationIds(List.of(1L, 5L))
                        .expectedPartitionWithRelationIds(List.of(1L, 2L, 3L))
                        .expectedPartitionWithRelationIds(List.of(1L, 4L))
                        .expectedPartitionWithRelationIds(List.of(1L, 5L)),
                testCase()
                        .description("Should create 2 partitions due to mixed repeating relation ids")
                        .partitionSize(10)
                        .duplicatedViolationWithRelationIds(List.of(1L, 2L, 3L))
                        .duplicatedViolationWithRelationIds(List.of(4L, 5L))
                        .duplicatedViolationWithRelationIds(List.of(1L, 6L))
                        .duplicatedViolationWithRelationIds(List.of(4L, 7L))
                        .duplicatedViolationWithRelationIds(List.of(4L, 5L))
                        .expectedPartitionWithRelationIds(List.of(1L, 2L, 3L, 4L, 5L))
                        .expectedPartitionWithRelationIds(List.of(1L, 6L, 4L, 7L))
                        .expectedPartitionWithRelationIds(List.of(4L, 5L)),
                testCase()
                        .description("Should not create partitions due to no violations")
                        .partitionSize(10)
        ).map(PartitionTestCase::build);
    }

    @Builder(builderMethodName = "testCase", builderClassName = "PartitionTestCase")
    private static Arguments buildTestCase(
            @NonNull String description,
            int partitionSize,
            @Singular("duplicatedViolationWithRelationIds") List<List<Long>> duplicatedViolationsWithRelationIds,
            @Singular("expectedPartitionWithRelationIds") List<List<Long>> expectedPartitionsWithRelationIds) {

        List<DuplicatedViolation> duplicatedViolations = createDuplicatedViolations(duplicatedViolationsWithRelationIds);
        List<List<InnerPolygonOsmoseViolation>> partitions = createPartitions(expectedPartitionsWithRelationIds);
        return Arguments.of(description, partitionSize, duplicatedViolations, partitions);
    }

    private static List<List<InnerPolygonOsmoseViolation>> createPartitions(List<List<Long>> expectedPartitionsWithRelationIds) {
        return expectedPartitionsWithRelationIds.stream()
                .map(DuplicatedViolationPartitionCreatorTest::createViolationsForRelationIds)
                .collect(Collectors.toList());
    }

    private static List<DuplicatedViolation> createDuplicatedViolations(List<List<Long>> duplicatedViolationsWithRelationIds) {
        return duplicatedViolationsWithRelationIds.stream()
                .map(DuplicatedViolationPartitionCreatorTest::createViolationsForRelationIds)
                .map(DuplicatedViolation::new)
                .collect(Collectors.toList());
    }

    private static List<InnerPolygonOsmoseViolation> createViolationsForRelationIds(List<Long> relationIds) {
        List<Long> exampleWaysIds = List.of(1L, 2L);
        return relationIds.stream()
                .map(relationId -> new InnerPolygonOsmoseViolation(EXAMPLE_RULE_ID,
                        new ViolatingOsmIds(List.of(relationId), exampleWaysIds)))
                .collect(Collectors.toList());
    }

    @BeforeEach
    void setUp() {
        partitionCreator = new DuplicatedViolationPartitionCreator();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldCreateCorrectPartitions(String description, int partitionSize, List<DuplicatedViolation> violations, List<List<InnerPolygonOsmoseViolation>> expectedViolationsInPartitions) {
        //  when
        Partitions<DuplicatedViolation> actualPartitions = partitionCreator.createPartitions(violations, partitionSize);

        //  then
        Assertions.assertThat(actualPartitions.getViolationsPartitions())
                .extracting(this::extractAllViolationsForEachPartition)
                .containsExactlyInAnyOrderElementsOf(expectedViolationsInPartitions);
    }

    private List<InnerPolygonOsmoseViolation> extractAllViolationsForEachPartition(List<DuplicatedViolation> duplicatedViolations) {
        return duplicatedViolations.stream()
                .map(DuplicatedViolation::getViolations)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
