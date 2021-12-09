package osm.bots.rings.inner.duplicates.osmapi.store;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataApi;
import lombok.RequiredArgsConstructor;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.RunParameters;

@Configuration
@RequiredArgsConstructor
class StoreConfiguration {

    @Bean
    OsmFixUploader getChangesetUploader(RunParameters runParameters) {
        OsmWriteClient osmWriteClient = new OsmWriteClient(getWriteDataApi(
                runParameters.getToken(),
                runParameters.getTokenSecret(),
                runParameters.getConsumerKey(),
                runParameters.getConsumerSecret(),
                runParameters.getOpenstreetmapApiUrl()));
        return new OsmFixUploader(osmWriteClient, runParameters.getOsmDiscussionPage(), runParameters.getOsmWikiDocumentationPage());
    }

    private MapDataApi getWriteDataApi(String token, String tokenSecret, String consumerKey, String consumerSecret, String openstreetmapApiUrl) {
        OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(token, tokenSecret);
        OsmConnection osmConnection = new OsmConnection(openstreetmapApiUrl, "user_agent", consumer);
        return new MapDataApi(osmConnection);
    }
}
