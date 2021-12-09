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
        OsmFetchClient osmFetchClient = new OsmFetchClient(
                getReadMapDataApi(parameters.getOpenstreetmapApiUrl())
        );
        return new OsmDataFetcher(osmFetchClient);
    }

    private MapDataApi getReadMapDataApi(String openstreetmapApiUrl) {
        OsmConnection connection = new OsmConnection(openstreetmapApiUrl, "user-agent", null);
        return new MapDataApi(connection);
    }
}
