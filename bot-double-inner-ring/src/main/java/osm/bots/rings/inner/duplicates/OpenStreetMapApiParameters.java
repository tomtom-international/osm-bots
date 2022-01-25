package osm.bots.rings.inner.duplicates;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With(value = AccessLevel.PACKAGE)
public class OpenStreetMapApiParameters {

    static final String MISSING_API_URL_MESSAGE = "Open Street Map API url is required";
    static final String MISSING_TOKEN_MESSAGE = "Open Street Map auth token is required";
    static final String MISSING_TOKEN_SECRET_MESSAGE = "Open Street Map auth token secret is required";
    static final String MISSING_CONSUMER_KEY_MESSAGE = "Open Street Map auth consumer key is required";
    static final String MISSING_CONSUMER_SECRET_MESSAGE = "Open Street Map auth consumer secret is required";

    @NotBlank(message = MISSING_API_URL_MESSAGE)
    private String url;

    @Valid
    private OpenStreetMapApiParameters.OpenStreetMapApiCredential credentials;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @With(value = AccessLevel.PACKAGE)
    public static class OpenStreetMapApiCredential {

        @NotBlank(message = OpenStreetMapApiParameters.MISSING_TOKEN_MESSAGE)
        private String token;
        @NotBlank(message = OpenStreetMapApiParameters.MISSING_TOKEN_SECRET_MESSAGE)
        private String tokenSecret;
        @NotBlank(message = OpenStreetMapApiParameters.MISSING_CONSUMER_KEY_MESSAGE)
        private String consumerKey;
        @NotBlank(message = OpenStreetMapApiParameters.MISSING_CONSUMER_SECRET_MESSAGE)
        private String consumerSecret;

    }

}
