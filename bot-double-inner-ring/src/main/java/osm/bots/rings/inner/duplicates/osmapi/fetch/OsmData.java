package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.data.Relation;
import lombok.Value;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Value
class OsmData {

    private static final String OSM_DATA_INFO_FORMAT = "{relation: %d wayIds: %s}";
    Relation relation;
    List<WayWithParentRelations> waysWithParentRelations;

    @Override
    public String toString() {
        return format(OSM_DATA_INFO_FORMAT, getRelationIdOrNull(), getFetchedWayIds());
    }

    private String getFetchedWayIds() {
        try {
            return waysWithParentRelations.stream()
                    .map(way -> way.getWay().getId())
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
        } catch (NullPointerException e) {
            return null;
        }
    }

    private Long getRelationIdOrNull() {
        if (relation == null) {
            return null;
        } else {
            return relation.getId();
        }
    }
}
