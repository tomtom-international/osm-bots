package osm.bots.rings.inner.duplicates.osmapi.fetch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmose.DuplicatedInnerPolygonViolation;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class FetchClient {

    private final OsmDataFetcher osmDataFetcher;

    public Optional<ViolatingOsmData> fetchDataForViolation(DuplicatedInnerPolygonViolation violation) {
        OsmData osmData = osmDataFetcher.fetch(violation);
        if (OsmDataCompletenessVerifier.isDataIncomplete(osmData)) {
            log.warn("Incomplete OsmData: {}", osmData);
            return Optional.empty();
        }
        return OsmDataConverter.convert(osmData);
    }
}
