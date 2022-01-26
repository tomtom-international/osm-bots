package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.RunParameters;

@Configuration
class FetchConfiguration {

    @Bean
    OsmDataClient osmDataClient(RunParameters parameters) {
        OsmConnection connection = new OsmConnection(parameters.getOpenStreetMapApi().getUrl(), "user-agent", null);
        MapDataApi mapDataApi = new MapDataApi(connection);
        return new OsmApiDataClient(mapDataApi);
    }

    @Bean
    OsmDataFetcher osmDataFetcher(OsmDataClient osmDataClient) {
        return new OsmDataFetcher(osmDataClient);
    }

    @Bean
    FetchClient fetchClient(OsmDataFetcher osmDataFetcher) {
        return new FetchClient(osmDataFetcher);
    }

}
