package osm.bots.innerringbot.utils;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataApi;
import de.westnordost.osmapi.map.data.Relation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Disabled
class UserAuthorizationTest {

    private static final String CONSUMER_KEY = "consumer_key";
    private static final String CONSUMER_SECRET = "consumer_secret";
    private static final String CONSUMER_TOKEN = "consumer_token";
    private static final String CONSUMER_TOKEN_SECRET = "consumer_token_secret";

    private static final String REQUEST_TOKEN_URL = "https://www.openstreetmap.org/oauth/request_token";
    private static final String ACCESS_TOKEN_URL = "https://www.openstreetmap.org/oauth/access_token";
    private static final String AUTHORIZATION_WEBSITE_URL = "https://www.openstreetmap.org/oauth/authorize";
    private static final String OPENSTREETMAP_ORG_API_0_6 = "https://api.openstreetmap.org/api/0.6/";

    private static final long EXAMPLE_RELATION_ID = 10633691L;

    /**
     * This test for provided consumer-key and consumer-secret-key, allows to retrieve consumer-token and consumer-secret,
     * which later on can be used to authorize requests to OSM.
     * Required parameters:
     * CONSUMER_KEY key generated for OAuth1 for user OSM account
     * CONSUMER_SECRET secret generated for OAuth1 for user OSM account
     */
    @SuppressWarnings("java:S2699")
    @SneakyThrows
    @Test
    void shouldRetrieveConsumerTokenAndSecret() {
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        DefaultOAuthProvider provider = new DefaultOAuthProvider(
                REQUEST_TOKEN_URL,
                ACCESS_TOKEN_URL,
                AUTHORIZATION_WEBSITE_URL);
        String authorizationUrl = provider.retrieveRequestToken(consumer, "");
        log.info("Authorization URL: {}", authorizationUrl);
        /*
          Please stop with debug here, and copy a value of the authorizationUrl. Next, please go to Your internet browser and paste there this authorizationUrl.
          Please follow the instructions in the browser. When procedure in done, finalize the test.
         */
        provider.retrieveAccessToken(consumer, null);
        consumer.setTokenWithSecret(consumer.getToken(), consumer.getTokenSecret());
        log.info("Consumer-token: {}", consumer.getToken());
        log.info("Consumer-secret: {}", consumer.getTokenSecret());
    }

    /**
     * This test verifies that for provided consumer-key, consumer-secret-key, consumer-token and consumer-secret
     * it is possible to perform an authorized download of data from OSM API.
     * Required parameters:
     * CONSUMER_KEY key generated for OAuth1 for user OSM account
     * CONSUMER_SECRET secret key generated for OAuth1 for user OSM account
     * CONSUMER_TOKEN token generated in previous test
     * CONSUMER_TOKEN_SECRET token secret generated in previous test
     */
    @SneakyThrows
    @Test
    void shouldCheckIfTokenAndTokenSecretWorks()  {
        //  given
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        consumer.setTokenWithSecret(CONSUMER_TOKEN, CONSUMER_TOKEN_SECRET);
        OsmConnection osmConnection = new OsmConnection(OPENSTREETMAP_ORG_API_0_6, "osm-bot", consumer);
        MapDataApi mapDataApi = new MapDataApi(osmConnection);

        //  when
        Relation relation = mapDataApi.getRelation(EXAMPLE_RELATION_ID);

        //  then
        assertThat(relation.getId()).isEqualTo(EXAMPLE_RELATION_ID);
    }
}

