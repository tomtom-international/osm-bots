package osm.bots.rings.inner.duplicates.osmose;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
class DuplicatedViolationFilter {

    List<InnerPolygonOsmoseViolation> findUniqueViolations(List<InnerPolygonOsmoseViolation> violations) {
        DeduplicateWorker deduplicateWorker = new DeduplicateWorker(violations);
        List<InnerPolygonOsmoseViolation> filteredViolations = deduplicateWorker.findUniqueViolations();
        log.info("{} violations were filtered out due to common way ID in read violations", violations.size() - filteredViolations.size());
        return filteredViolations;
    }

    List<DuplicatedViolation> findDuplicatedViolations(List<InnerPolygonOsmoseViolation> violations) {
        DeduplicateWorker deduplicateWorker = new DeduplicateWorker(violations);
        List<DuplicatedViolation> duplicated = deduplicateWorker.findDuplicatedViolations();
        log.info("{} duplicated way IDs were detected due to common way ID in read violations", duplicated.size());
        return duplicated;
    }

    private static class DeduplicateWorker {

        List<InnerPolygonOsmoseViolation> filteredViolations;

        private DeduplicateWorker(List<InnerPolygonOsmoseViolation> violations) {
            this.filteredViolations = new ArrayList<>(violations);
        }

        private List<InnerPolygonOsmoseViolation> findUniqueViolations() {
            Map<Long, List<InnerPolygonOsmoseViolation>> violationsGroupedByWayId = new HashMap<>();
            filteredViolations.forEach(violation -> groupViolationsByWayId(violation, violationsGroupedByWayId));
            removeDuplicatedViolations(violationsGroupedByWayId);
            return filteredViolations;
        }

        private List<DuplicatedViolation> findDuplicatedViolations() {
            Map<Long, List<InnerPolygonOsmoseViolation>> violationsGroupedByWayId = new HashMap<>();
            filteredViolations.forEach(violation -> groupViolationsByWayId(violation, violationsGroupedByWayId));
            return violationsGroupedByWayId
                    .values()
                    .stream()
                    .filter(duplicatedInnerPolygonViolations -> duplicatedInnerPolygonViolations.size() > 1)
                    .filter(this::relationIdsAreNotRepeating)
                    .map(DuplicatedViolation::new)
                    .collect(Collectors.toList());
        }

        private boolean relationIdsAreNotRepeating(List<InnerPolygonOsmoseViolation> duplicatedViolations) {
            Set<Long> uniqueRelationIds = duplicatedViolations.stream()
                    .map(InnerPolygonOsmoseViolation::getViolatingRelationsIds)
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());
            boolean relationIdsAreRepeating = duplicatedViolations.size() != uniqueRelationIds.size();
            if (relationIdsAreRepeating) {
                log.info("Detected repeating relation ids in {}", duplicatedViolations);
            }
            return !relationIdsAreRepeating;
        }

        private void removeDuplicatedViolations(Map<Long, List<InnerPolygonOsmoseViolation>> violationsGroupedByWayId) {
            violationsGroupedByWayId
                    .values()
                    .stream()
                    .filter(duplicatedInnerPolygonViolations -> duplicatedInnerPolygonViolations.size() > 1)
                    .forEach(filteredViolations::removeAll);
        }

        private void groupViolationsByWayId(InnerPolygonOsmoseViolation violation, Map<Long, List<InnerPolygonOsmoseViolation>> violationsGroupedByWayId) {
            violation.getPairOfViolatingWaysIds()
                    .forEach(wayId -> violationsGroupedByWayId.computeIfAbsent(wayId, key -> new ArrayList<>()).add(violation));
        }
    }
}
