package osm.bots.rings.inner.duplicates.fix.generator;

import de.westnordost.osmapi.map.data.OsmWay;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

import java.util.List;
import java.util.Map;

public class ReplaceWayTagsFixGenerator implements FixGenerator {

    @Override
    public ViolationFix generateFix(ViolatingOsmData violatingOsmData) {
        OsmWay wayToBeDeleted = (OsmWay) violatingOsmData.getDuplicatingWay().getWay();
        OsmWay wayToBeUpdated = (OsmWay) violatingOsmData.getInnerRingWay().getWay();
        Map<String, String> tagsToBeMoved = wayToBeDeleted.getTags();
        wayToBeUpdated.getTags().putAll(tagsToBeMoved);
        wayToBeUpdated.setModified(true);
        wayToBeDeleted.setDeleted(true);

        return new ViolationFix(List.of(wayToBeDeleted, wayToBeUpdated));
    }
}
