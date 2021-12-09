package osm.bots.rings.inner.duplicates;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Data
@Configuration
@ConfigurationProperties(prefix = "run.parameters")
public class RunParameters {

    private Path pathToViolationsFile;
    private String osmDiscussionPage;
    private String osmWikiDocumentationPage;
    private String openstreetmapApiUrl;
    private String token;
    private String tokenSecret;
    private String consumerKey;
    private String consumerSecret;
    private Integer maxViolationsPerChangeset;
}
