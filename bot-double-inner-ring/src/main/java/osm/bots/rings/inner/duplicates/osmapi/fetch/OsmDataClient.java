package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.MapDataApi;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import net.jodah.failsafe.Failsafe;
import osm.bots.rings.inner.duplicates.utils.RetryPolicyFactory;
import osm.bots.rings.inner.duplicates.utils.TimeoutFactory;

import java.util.List;

class OsmDataClient {

    private final MapDataApi mapDataApi;

    OsmDataClient(MapDataApi mapDataApi) {
        this.mapDataApi = mapDataApi;
    }

    Way getWay(long wayId) {
        return Failsafe.with(
                TimeoutFactory.getClientConnectionTimeout(),
                RetryPolicyFactory.getClientConnectionRetryPolicy())
                .get(() -> mapDataApi.getWay(wayId));
    }

    Relation getRelation(long relationId) {
        return Failsafe.with(
                TimeoutFactory.getClientConnectionTimeout(),
                RetryPolicyFactory.getClientConnectionRetryPolicy())
                .get(() -> mapDataApi.getRelation(relationId));
    }

    List<Relation> getRelationsForWay(long wayId) {
        return Failsafe.with(
                TimeoutFactory.getClientConnectionTimeout(),
                RetryPolicyFactory.getClientConnectionRetryPolicy())
                .get(() -> mapDataApi.getRelationsForWay(wayId));
    }
}
