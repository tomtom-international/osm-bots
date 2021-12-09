package osm.bots.rings.inner.duplicates.osmapi.model;

import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class WayWithParentRelations {

    Way way;
    List<Relation> parentRelations;

    public List<Long> getNodesOfWay() {
        return this.way.getNodeIds();
    }

    public List<Long> getIdsOfParentRelations() {
        return parentRelations.stream()
                .map(Element::getId)
                .collect(Collectors.toList());
    }
}
