package osm.bots.rings.inner.duplicates.osmose;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
class OsmoseViolationsValidator {

    private static final int DUPLICATE_INNER_POLYGON_OSMOSE_RULE_ID = 1170;

    List<InnerPolygonOsmoseViolation> getValidViolations(List<InnerPolygonOsmoseViolation> violations) {
        List<InnerPolygonOsmoseViolation> filteredViolations = violations.stream()
                .filter(shouldHaveProperRuleId())
                .filter(shouldHaveExactlyTwoWays())
                .filter(shouldHaveExactlyOneRelation())
                .collect(Collectors.toList());

        log.info("Filtered out {} violations out of {} during Osmose json validation",
                violations.size() - filteredViolations.size(),
                violations.size());
        return filteredViolations;
    }

    private Predicate<InnerPolygonOsmoseViolation> shouldHaveProperRuleId() {
        return violation -> violation.getOsmoseRuleId() == DUPLICATE_INNER_POLYGON_OSMOSE_RULE_ID;
    }

    private Predicate<InnerPolygonOsmoseViolation> shouldHaveExactlyTwoWays() {
        return violation -> violation.getViolatingWaysIds().size() == 2;
    }

    private Predicate<InnerPolygonOsmoseViolation> shouldHaveExactlyOneRelation() {
        return violation -> violation.getViolatingRelationsIds().size() == 1;
    }
}
