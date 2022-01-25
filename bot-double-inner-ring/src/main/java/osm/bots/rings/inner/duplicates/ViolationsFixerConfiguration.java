package osm.bots.rings.inner.duplicates;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.osmapi.fetch.FetchClient;
import osm.bots.rings.inner.duplicates.osmapi.fix.ReplaceWayTagsFixGenerator;
import osm.bots.rings.inner.duplicates.osmapi.store.FixUploader;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolationsFetcher;
import osm.bots.rings.inner.duplicates.verifiers.DataVerifier;

@Configuration
class ViolationsFixerConfiguration {

    @Bean
    ViolationsFixer violationFixer(
            OsmoseViolationsFetcher osmoseViolationsFetcher,
            FetchClient fetchClient,
            DataVerifier dataVerifier,
            ReplaceWayTagsFixGenerator replaceWayTagsFixGenerator,
            FixUploader fixUploader,
            RunParameters runParameters) {
        return new ViolationsFixer(osmoseViolationsFetcher,
                fetchClient,
                dataVerifier,
                replaceWayTagsFixGenerator,
                fixUploader,
                runParameters.getMaxViolationsPerChangeset());
    }
}
