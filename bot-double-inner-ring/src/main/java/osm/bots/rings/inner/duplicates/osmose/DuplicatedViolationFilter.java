package osm.bots.rings.inner.duplicates.osmose;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Slf4j
class DuplicatedViolationFilter {

    List<InnerPolygonOsmoseViolation> findUniqueViolations(List<InnerPolygonOsmoseViolation> violations) {
        Map<Long, List<InnerPolygonOsmoseViolation>> violationsGroupedByWayId = groupViolationByWayId(violations);
        List<InnerPolygonOsmoseViolation> uniqueViolations = uniqueViolations(violations, violationsGroupedByWayId);
        log.info("{} violations were filtered out due to common way ID in read violations", violations.size() - uniqueViolations.size());
        return uniqueViolations;
    }

    List<DuplicatedViolation> findDuplicatedViolations(List<InnerPolygonOsmoseViolation> violations) {
        Map<Long, List<InnerPolygonOsmoseViolation>> violationsGroupedByWayId = groupViolationByWayId(violations);
        List<DuplicatedViolation> duplicatedViolations = duplicatedViolations(violationsGroupedByWayId);
        log.info("{} duplicated way IDs were detected due to common way ID in read violations", duplicatedViolations.size());
        return duplicatedViolations;
    }

    private static List<InnerPolygonOsmoseViolation> uniqueViolations(List<InnerPolygonOsmoseViolation> violations,
                                                                      Map<Long, List<InnerPolygonOsmoseViolation>> violationsGroupedByWayId) {
        List<InnerPolygonOsmoseViolation> duplicatedViolations =
                duplicatedViolations(violationsGroupedByWayId).stream()
                        .flatMap(duplicatedViolation -> duplicatedViolation.getViolations().stream())
                        .collect(toList());
        List<InnerPolygonOsmoseViolation> result = new ArrayList<>(violations);
        result.removeAll(duplicatedViolations);
        return result;
    }

    private static List<DuplicatedViolation> duplicatedViolations(Map<Long, List<InnerPolygonOsmoseViolation>> violationsGroupedByWayId) {
        return violationsGroupedByWayId.values().stream()
                .filter(duplicatedInnerPolygonViolations -> duplicatedInnerPolygonViolations.size() > 1)
                .map(DuplicatedViolation::new)
                .collect(toList());
    }

    private static Map<Long, List<InnerPolygonOsmoseViolation>> groupViolationByWayId(List<InnerPolygonOsmoseViolation> violations) {
        return violations.stream()
                .flatMap(
                        violation -> violation.getPairOfViolatingWaysIds().stream()
                                .map(wayId -> new ViolationWay(wayId, violation))
                ).collect(groupingBy(
                        ViolationWay::getWayId,
                        mapping(ViolationWay::getViolation, toList())));
    }

    @Value
    private static class ViolationWay {

        long wayId;
        InnerPolygonOsmoseViolation violation;
    }

}
