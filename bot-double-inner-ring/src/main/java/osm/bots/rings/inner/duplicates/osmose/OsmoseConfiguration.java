package osm.bots.rings.inner.duplicates.osmose;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.RunParameters;

@Configuration
class OsmoseConfiguration {

    @Bean
    OsmoseViolationsFetcher osmoseViolationsFetcher(
            OsmoseViolationsReader osmoseViolationsReader,
            OsmoseViolationsValidator osmoseViolationsValidator,
            DuplicatedViolationFilter duplicatedViolationFilter,
            DuplicatedViolationPartitionCreator duplicatedViolationPartitionCreator,
            RunParameters runParameters) {
        return new OsmoseViolationsFetcher(osmoseViolationsReader,
                osmoseViolationsValidator,
                duplicatedViolationFilter,
                duplicatedViolationPartitionCreator,
                runParameters.getMaxViolationsPerChangeset());
    }

    @Bean
    OsmoseViolationsReader violationReader(RunParameters runParameters) {
        return new OsmoseViolationsJsonReader(runParameters.getPathToViolationsFile());
    }

    @Bean
    OsmoseViolationsValidator osmoseViolationsValidator() {
        return new OsmoseViolationsValidator();
    }

    @Bean
    DuplicatedViolationFilter duplicatedViolationFilter() {
        return new DuplicatedViolationFilter();
    }

    @Bean
    DuplicatedViolationPartitionCreator duplicatedViolationPartitionCreator() {
        return new DuplicatedViolationPartitionCreator();
    }
}
