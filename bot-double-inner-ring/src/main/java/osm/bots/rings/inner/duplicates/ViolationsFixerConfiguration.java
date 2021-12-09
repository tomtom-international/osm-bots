package osm.bots.rings.inner.duplicates;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.osmapi.fetch.OsmDataFetcher;
import osm.bots.rings.inner.duplicates.osmapi.fix.ViolationFixGenerator;
import osm.bots.rings.inner.duplicates.osmapi.store.OsmFixUploader;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolationsFetcher;
import osm.bots.rings.inner.duplicates.verifiers.DataVerifier;

@Configuration
class ViolationsFixerConfiguration {

    @Bean
    ViolationsFixer getViolationFixer(
            OsmoseViolationsFetcher osmoseViolationsFetcher,
            OsmDataFetcher osmDataFetcher,
            DataVerifier dataVerifier,
            ViolationFixGenerator violationFixGenerator,
            OsmFixUploader osmFixUploader,
            RunParameters runParameters) {
        return new ViolationsFixer(osmoseViolationsFetcher,
                osmDataFetcher,
                dataVerifier,
                violationFixGenerator,
                osmFixUploader,
                runParameters.getMaxViolationsPerChangeset());
    }
}
