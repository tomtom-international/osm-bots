package osm.bots.rings.inner.duplicates.fix;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import osm.bots.rings.inner.duplicates.fix.generator.ReplaceRelationMemberFixGenerator;
import osm.bots.rings.inner.duplicates.fix.generator.ReplaceWayTagsFixGenerator;
import osm.bots.rings.inner.duplicates.osmapi.fetch.FetchClient;
import osm.bots.rings.inner.duplicates.osmapi.store.FixUploader;
import osm.bots.rings.inner.duplicates.verifiers.DataVerifier;

@Configuration
class ViolationsFixerConfiguration {

    @Bean
    DuplicatedViolationsFixer duplicatedViolationsFixer(
            FetchClient fetchClient,
            DataVerifier dataVerifier,
            ReplaceRelationMemberFixGenerator replaceRelationMemberFixGenerator,
            FixUploader fixUploader) {
        return new DuplicatedViolationsFixer(fetchClient, replaceRelationMemberFixGenerator, fixUploader, dataVerifier);
    }

    @Bean
    UniqueViolationsFixer uniqueViolationsFixer(
            FetchClient fetchClient,
            DataVerifier dataVerifier,
            ReplaceWayTagsFixGenerator replaceWayTagsFixGenerator,
            FixUploader fixUploader) {
        return new UniqueViolationsFixer(fetchClient, replaceWayTagsFixGenerator, fixUploader, dataVerifier);
    }
}
