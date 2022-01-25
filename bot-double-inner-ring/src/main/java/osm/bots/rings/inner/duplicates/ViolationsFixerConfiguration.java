package osm.bots.rings.inner.duplicates;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.osmapi.fetch.FetchClient;
import osm.bots.rings.inner.duplicates.osmapi.fix.ViolationFixGenerator;
import osm.bots.rings.inner.duplicates.osmapi.store.FixUploader;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolationsFetcher;
import osm.bots.rings.inner.duplicates.verifiers.DataVerifier;

@Configuration
class ViolationsFixerConfiguration {

    @Bean
    ViolationsFixer getViolationFixer(
            OsmoseViolationsFetcher osmoseViolationsFetcher,
            FetchClient fetchClient,
            DataVerifier dataVerifier,
            ViolationFixGenerator violationFixGenerator,
            FixUploader fixUploader,
            RunParameters runParameters) {
        return new ViolationsFixer(osmoseViolationsFetcher,
                fetchClient,
                dataVerifier,
                violationFixGenerator,
                fixUploader,
                runParameters.getMaxViolationsPerChangeset());
    }
}
