package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;

import java.util.List;

interface OsmDataClient {

    Way getWay(long wayId);

    Relation getRelation(long relationId);

    List<Relation> getRelationsForWay(long wayId);
}
