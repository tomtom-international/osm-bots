package osm.bots.rings.inner.duplicates.osmose;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OsmoseConfiguration {

    @Bean
    OsmoseViolationsFetcher getOsmoseViolationsFetcher() {
        return new OsmoseViolationsFetcher(getViolationJsonReader(), getOsmoseViolationsValidator());
    }

    @Bean
    OsmoseViolationsJsonReader getViolationJsonReader() {
        return new OsmoseViolationsJsonReader();
    }

    @Bean
    OsmoseViolationsValidator getOsmoseViolationsValidator() {
        return new OsmoseViolationsValidator();
    }
}
