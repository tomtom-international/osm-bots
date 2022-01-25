package osm.bots.rings.inner.duplicates.osmose;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OsmoseConfiguration {

    @Bean
    OsmoseViolationsFetcher osmoseViolationsFetcher(
            OsmoseViolationsReader osmoseViolationsReader,
            OsmoseViolationsValidator osmoseViolationsValidator,
            DuplicatedViolationFilter duplicatedViolationFilter) {
        return new OsmoseViolationsFetcher(osmoseViolationsReader, osmoseViolationsValidator, duplicatedViolationFilter);
    }

    @Bean
    OsmoseViolationsReader violationReader() {
        return new OsmoseViolationsJsonReader();
    }

    @Bean
    OsmoseViolationsValidator osmoseViolationsValidator() {
        return new OsmoseViolationsValidator();
    }

    @Bean
    DuplicatedViolationFilter duplicatedViolationFilter() {
        return new DuplicatedViolationFilter();
    }
}
