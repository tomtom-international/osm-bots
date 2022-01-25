package osm.bots.rings.inner.duplicates.osmose;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.fix.Partitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DuplicatedViolationPartitionCreator {

    public Partitions<DuplicatedViolation> createPartitions(List<DuplicatedViolation> allDuplicatedViolations, int maxViolationsPerChangeset) {
        PartitionWorker worker = new PartitionWorker(maxViolationsPerChangeset);
        return new Partitions<>(worker.createPartitions(allDuplicatedViolations));
    }

    private static class PartitionWorker {

        private final int maxViolationsPerChangeset;
        private final List<List<DuplicatedViolation>> partitionedViolations;

        private PartitionWorker(int maxViolationsPerChangeset) {
            this.maxViolationsPerChangeset = maxViolationsPerChangeset;
            partitionedViolations = new ArrayList<>();
        }

        private Collection<List<DuplicatedViolation>> createPartitions(List<DuplicatedViolation> allDuplicatedViolations) {
            for (DuplicatedViolation duplicatedViolation : allDuplicatedViolations) {
                boolean successfullyAddedViolation = addViolationsToAlreadyExistingPartition(duplicatedViolation);
                if (!successfullyAddedViolation) {
                    addNewPartition(duplicatedViolation);
                }
            }
            log.info("{} duplicated violations has been split into {} partitions", allDuplicatedViolations.size(), partitionedViolations.size());
            return partitionedViolations;
        }

        private boolean addViolationsToAlreadyExistingPartition(DuplicatedViolation violation) {
            if (partitionedViolations.isEmpty()) {
                return false;
            }
            for (List<DuplicatedViolation> partition : partitionedViolations) {
                if (possibleToAddViolationsToPartition(violation, partition)) {
                    partition.add(violation);
                    return true;
                }
            }
            return false;
        }

        private boolean possibleToAddViolationsToPartition(DuplicatedViolation violation, List<DuplicatedViolation> partition) {
            return partitionSizeIsLessThanLimit(partition)
                    && relationIdsAreNotRepeating(getRelationsIds(partition), getRelationsIds(violation));
        }

        private boolean relationIdsAreNotRepeating(List<Long> alreadyPartitionedRelationsIds, List<Long> candidateRelationsIds) {
            return alreadyPartitionedRelationsIds.stream().noneMatch(candidateRelationsIds::contains);
        }

        private List<Long> getRelationsIds(DuplicatedViolation violation) {
            return violation.getViolations()
                    .stream()
                    .map(InnerPolygonOsmoseViolation::getViolatingRelationId)
                    .collect(Collectors.toList());
        }

        private List<Long> getRelationsIds(List<DuplicatedViolation> violations) {
            return violations.stream()
                    .map(DuplicatedViolation::getViolations)
                    .flatMap(List::stream)
                    .map(InnerPolygonOsmoseViolation::getViolatingRelationId)
                    .collect(Collectors.toList());
        }

        private boolean partitionSizeIsLessThanLimit(List<DuplicatedViolation> partition) {
            long countOfViolationsInPartition = partition.stream()
                    .mapToLong(duplicatedViolation -> duplicatedViolation.getViolations().size())
                    .sum();
            return countOfViolationsInPartition < maxViolationsPerChangeset;
        }

        private void addNewPartition(DuplicatedViolation duplicatedViolation) {
            ArrayList<DuplicatedViolation> newPartition = new ArrayList<>();
            newPartition.add(duplicatedViolation);
            partitionedViolations.add(newPartition);
        }
    }
}
