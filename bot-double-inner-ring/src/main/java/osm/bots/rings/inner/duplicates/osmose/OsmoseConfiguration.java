package osm.bots.rings.inner.duplicates.osmose;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OsmoseConfiguration {

    @Bean
    OsmoseViolationsFetcher osmoseViolationsFetcher(
            OsmoseViolationsJsonReader osmoseViolationsJsonReader,
            OsmoseViolationsValidator osmoseViolationsValidator,
            DuplicatedViolationFilter duplicatedViolationFilter) {
        return new OsmoseViolationsFetcher(osmoseViolationsJsonReader, osmoseViolationsValidator, duplicatedViolationFilter);
    }

    @Bean
    OsmoseViolationsJsonReader violationJsonReader() {
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
