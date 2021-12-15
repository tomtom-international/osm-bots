package osm.bots.rings.inner.duplicates.osmose;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
class DuplicatedViolationFilter {

    List<DuplicatedInnerPolygonViolation> deduplicate(List<DuplicatedInnerPolygonViolation> violations) {
        DeduplicateWorker deduplicateWorker = new DeduplicateWorker(violations);
        List<DuplicatedInnerPolygonViolation> filteredViolations = deduplicateWorker.getFilteredViolations();
        log.info("{} violations were filtered out due to common way ID in read violations", violations.size() - filteredViolations.size());
        return filteredViolations;
    }

    @Value
    private static class DeduplicateWorker {

        List<DuplicatedInnerPolygonViolation> filteredViolations;

        private DeduplicateWorker(List<DuplicatedInnerPolygonViolation> violations) {
            this.filteredViolations = new ArrayList<>(violations);
            deduplicate();
        }

       private void deduplicate() {
            Map<Long, List<DuplicatedInnerPolygonViolation>> violationsGroupedByWayId = new HashMap<>();
            filteredViolations.forEach(violation -> groupViolationsByWayId(violation, violationsGroupedByWayId));
            removeDuplicatedViolations(violationsGroupedByWayId);
        }

        private void removeDuplicatedViolations(Map<Long, List<DuplicatedInnerPolygonViolation>> violationsGroupedByWayId) {
            violationsGroupedByWayId
                    .values()
                    .stream()
                    .filter(duplicatedInnerPolygonViolations -> duplicatedInnerPolygonViolations.size() > 1)
                    .forEach(filteredViolations::removeAll);
        }

        private void groupViolationsByWayId(DuplicatedInnerPolygonViolation violation, Map<Long, List<DuplicatedInnerPolygonViolation>> violationsGroupedByWayId) {
            violation.getPairOfViolatingWaysIds()
                    .forEach(violationId -> violationsGroupedByWayId.computeIfAbsent(violationId, key -> new ArrayList<>()).add(violation));
        }
    }
}
