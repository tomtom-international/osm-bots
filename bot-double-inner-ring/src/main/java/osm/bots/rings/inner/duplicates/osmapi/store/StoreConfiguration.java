package osm.bots.rings.inner.duplicates.osmapi.store;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataApi;
import lombok.RequiredArgsConstructor;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.OpenStreetMapApiParameters;
import osm.bots.rings.inner.duplicates.OpenStreetMapApiParameters.OpenStreetMapApiCredential;
import osm.bots.rings.inner.duplicates.RunParameters;

import static java.util.Objects.requireNonNull;

@Configuration
@RequiredArgsConstructor
class StoreConfiguration {

    @Bean
    FixUploader getChangesetUploader(RunParameters runParameters) {
        if (runParameters.isUploadFixes()) {
            OsmWriteClient osmWriteClient = new OsmWriteClient(getWriteDataApi(runParameters.getOpenStreetMapApi()));
            return new OsmApiFixUploader(osmWriteClient, runParameters.getOsmDiscussionPage(), runParameters.getOsmWikiDocumentationPage());
        } else {
            return new LogOnlyFixUploader();
        }
    }

    private MapDataApi getWriteDataApi(OpenStreetMapApiParameters openStreetMapApiParameters) {
        OpenStreetMapApiCredential credentials =
                requireNonNull(openStreetMapApiParameters.getCredentials(), "Open Street Map API credentials cannot be null");
        OAuthConsumer consumer = new DefaultOAuthConsumer(credentials.getConsumerKey(), credentials.getConsumerSecret());
        consumer.setTokenWithSecret(credentials.getToken(), credentials.getTokenSecret());
        OsmConnection osmConnection = new OsmConnection(openStreetMapApiParameters.getUrl(), "user_agent", consumer);
        return new MapDataApi(osmConnection);
    }
}
