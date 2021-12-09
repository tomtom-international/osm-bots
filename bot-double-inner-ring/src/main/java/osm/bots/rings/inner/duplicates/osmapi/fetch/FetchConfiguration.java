package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.RunParameters;

@Configuration
@RequiredArgsConstructor
class FetchConfiguration {

    @Bean
    OsmDataFetcher getOsmDataFetcher(RunParameters parameters) {
        OsmDataClient osmDataClient = new OsmDataClient(
                getReadMapDataApi(parameters.getOpenstreetmapApiUrl())
        );
        return new OsmDataFetcher(osmDataClient);
    }

    @Bean
    FetchClient getFetchClient(OsmDataFetcher osmDataFetcher) {
        return new FetchClient(osmDataFetcher);
    }

    private MapDataApi getReadMapDataApi(String openstreetmapApiUrl) {
        OsmConnection connection = new OsmConnection(openstreetmapApiUrl, "user-agent", null);
        return new MapDataApi(connection);
    }
}
