package osm.bots.rings.inner.duplicates;

import lombok.Value;
import osm.bots.rings.inner.duplicates.osmose.InnerPolygonOsmoseViolation;
import osm.bots.rings.inner.duplicates.osmose.ViolatingOsmIds;

import java.util.List;

@Value
class TestViolation {

    public static final int DOUBLE_INNER_POLYGON_RULE_ID = 1170;

    List<Long> relationIds;
    List<Long> waysIds;

    static TestViolation of(final Long relationId, final Long firstWayId, final Long secondWayId) {
        return new TestViolation(List.of(relationId), List.of(firstWayId, secondWayId));
    }

    InnerPolygonOsmoseViolation toDuplicatedInnerPolygonViolation() {
        return new InnerPolygonOsmoseViolation(DOUBLE_INNER_POLYGON_RULE_ID, new ViolatingOsmIds(relationIds, waysIds));
    }
}
