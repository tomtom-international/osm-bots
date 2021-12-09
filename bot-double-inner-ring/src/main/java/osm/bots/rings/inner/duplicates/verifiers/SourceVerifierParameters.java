package osm.bots.rings.inner.duplicates.verifiers;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Data
@Configuration
@ConfigurationProperties(prefix = "run.parameters.verifier.source")
class SourceVerifierParameters {

    private Collection<String> tagValues;
}
