package osm.bots.rings.inner.duplicates;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.fix.ViolationsFixer;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolationsFetcher;

import java.util.List;

@Configuration
class ApplicationConfiguration {

    @Bean
    ViolationsProcessor violationsProcessor(List<ViolationsFixer> violationsFixers, OsmoseViolationsFetcher osmoseViolationsFetcher) {
        return new ViolationsProcessor(violationsFixers, osmoseViolationsFetcher);
    }
}
