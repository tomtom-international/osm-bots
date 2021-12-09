package osm.bots.rings.inner.duplicates.osmapi.model;

import de.westnordost.osmapi.map.data.Relation;
import lombok.Value;

import java.util.List;

@Value
public class ViolatingOsmData {

    private static final String VIOLATING_IDS_FORMAT = "{relationId:%d, innerRingWayId:%d, duplicatingWayId:%d}";

    Relation relation;
    WayWithParentRelations innerRingWay;
    WayWithParentRelations duplicatingWay;

    public List<Long> getNodeIdsOfDuplicatingWay() {
        return this.innerRingWay.getNodesOfWay();
    }

    public List<Long> getNodeIdsOfInnerRingWay() {
        return this.duplicatingWay.getNodesOfWay();
    }

    @Override
    public String toString() {
        return String.format(VIOLATING_IDS_FORMAT,
                relation.getId(),
                innerRingWay.getWay().getId(),
                duplicatingWay.getWay().getId());
    }
}
