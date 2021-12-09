package osm.bots.rings.inner.duplicates.osmapi.store;

import de.westnordost.osmapi.map.MapDataApi;
import de.westnordost.osmapi.map.data.Element;
import net.jodah.failsafe.Failsafe;
import osm.bots.rings.inner.duplicates.utils.RetryPolicyFactory;
import osm.bots.rings.inner.duplicates.utils.TimeoutFactory;

import java.util.Map;

class OsmWriteClient {

    private final MapDataApi mapDataApi;

    OsmWriteClient(MapDataApi mapDataApi) {
        this.mapDataApi = mapDataApi;
    }

    long openChangeset(Map<String, String> tags) {
        return Failsafe.with(
                TimeoutFactory.getClientConnectionTimeout(),
                RetryPolicyFactory.getClientConnectionRetryPolicy())
                .get(() -> mapDataApi.openChangeset(tags));
    }

    void closeChangeset(long changesetId) {
        Failsafe.with(
                TimeoutFactory.getClientConnectionTimeout(),
                RetryPolicyFactory.getClientConnectionRetryPolicy())
                .run(() -> mapDataApi.closeChangeset(changesetId));
    }

    void uploadChanges(long changesetId, Iterable<Element> elements) {
        Failsafe.with(
                TimeoutFactory.getClientConnectionTimeout(),
                RetryPolicyFactory.getClientConnectionRetryPolicy())
                .run(() -> mapDataApi.uploadChanges(changesetId, elements, null));
    }
}
