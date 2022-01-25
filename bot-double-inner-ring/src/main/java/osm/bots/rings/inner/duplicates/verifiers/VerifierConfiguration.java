package osm.bots.rings.inner.duplicates.verifiers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
class VerifierConfiguration {

    @Bean
    DataVerifier dataVerifier(Collection<Verifier> verifiers) {
        return new DataVerifier(verifiers);
    }

    @Bean
    @ConditionalOnProperty(prefix = "run.parameters.verifier.ways-have-matching-nodes", name = "active")
    WaysHaveMatchingNodesVerifier waysHaveMatchingNodesVerifier() {
        return new WaysHaveMatchingNodesVerifier();
    }

    @Bean
    @ConditionalOnProperty(prefix = "run.parameters.verifier.duplicating-way-has-tags", name = "active")
    DuplicatingWayHasTagsVerifier duplicatingWayHasTagsVerifier() {
        return new DuplicatingWayHasTagsVerifier();
    }

    @Bean
    @ConditionalOnProperty(prefix = "run.parameters.verifier.duplicating-way-is-not-member-of-any-relation", name = "active")
    DuplicatingWayIsNotMemberOfAnyRelationVerifier duplicatingWayIsNotMemberOfAnyRelationVerifier() {
        return new DuplicatingWayIsNotMemberOfAnyRelationVerifier();
    }

    @Bean
    @ConditionalOnProperty(prefix = "run.parameters.verifier.inner-ring-way-has-no-tags", name = "active")
    InnerRingWayHasNoTagsVerifier innerRingWayHasNoTagsVerifier() {
        return new InnerRingWayHasNoTagsVerifier();
    }

    @Bean
    @ConditionalOnProperty(prefix = "run.parameters.verifier.inner-ring-way-is-only-member-of-violating-relation", name = "active")
    InnerRingWayIsOnlyMemberOfViolatingRelationVerifier innerRingWayIsOnlyMemberOfViolatingRelationVerifier() {
        return new InnerRingWayIsOnlyMemberOfViolatingRelationVerifier();
    }

    @Bean
    @ConditionalOnProperty(prefix = "run.parameters.verifier.source", name = "active")
    SourceVerifier sourceVerifier(SourceVerifierParameters parameters) {
        return new SourceVerifier(parameters.getTagValues());
    }
}
