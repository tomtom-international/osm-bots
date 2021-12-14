package osm.bots.rings.inner.duplicates.osmose;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OsmoseConfiguration {

    @Bean
    OsmoseViolationsFetcher getOsmoseViolationsFetcher(
            OsmoseViolationsJsonReader osmoseViolationsJsonReader,
            OsmoseViolationsValidator osmoseViolationsValidator,
            DuplicatedViolationFilter duplicatedViolationFilter) {
        return new OsmoseViolationsFetcher(osmoseViolationsJsonReader, osmoseViolationsValidator, duplicatedViolationFilter);
    }

    @Bean
    OsmoseViolationsJsonReader getViolationJsonReader() {
        return new OsmoseViolationsJsonReader();
    }

    @Bean
    OsmoseViolationsValidator getOsmoseViolationsValidator() {
        return new OsmoseViolationsValidator();
    }

    @Bean
    DuplicatedViolationFilter getDuplicatedViolationFilter() {
        return new DuplicatedViolationFilter();
    }
}
