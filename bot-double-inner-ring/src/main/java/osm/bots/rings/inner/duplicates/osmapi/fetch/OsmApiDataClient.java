package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.MapDataApi;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import net.jodah.failsafe.Failsafe;
import osm.bots.rings.inner.duplicates.utils.RetryPolicyFactory;
import osm.bots.rings.inner.duplicates.utils.TimeoutFactory;

import java.util.List;

class OsmApiDataClient implements OsmDataClient {

    private final MapDataApi mapDataApi;

    OsmApiDataClient(MapDataApi mapDataApi) {
        this.mapDataApi = mapDataApi;
    }

    @Override
    public Way getWay(long wayId) {
        return Failsafe.with(
                TimeoutFactory.getClientConnectionTimeout(),
                RetryPolicyFactory.getClientConnectionRetryPolicy())
                .get(() -> mapDataApi.getWay(wayId));
    }

    @Override
    public Relation getRelation(long relationId) {
        return Failsafe.with(
                TimeoutFactory.getClientConnectionTimeout(),
                RetryPolicyFactory.getClientConnectionRetryPolicy())
                .get(() -> mapDataApi.getRelation(relationId));
    }

    @Override
    public List<Relation> getRelationsForWay(long wayId) {
        return Failsafe.with(
                TimeoutFactory.getClientConnectionTimeout(),
                RetryPolicyFactory.getClientConnectionRetryPolicy())
                .get(() -> mapDataApi.getRelationsForWay(wayId));
    }
}
