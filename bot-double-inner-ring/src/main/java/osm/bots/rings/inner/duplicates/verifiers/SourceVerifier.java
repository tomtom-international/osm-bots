package osm.bots.rings.inner.duplicates.verifiers;

import de.westnordost.osmapi.map.data.Element;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
class SourceVerifier extends Verifier {

    private final Collection<String> configuredTagValues;

    SourceVerifier(Collection<String> tagValues) {
        this.configuredTagValues = tagValues;
    }

    @Override
    boolean isMatchingVerifierCriteria(ViolatingOsmData violatingOsmData) {
        return hasValidSourceTag(violatingOsmData.getRelation(), violatingOsmData)
                && hasValidSourceTag(violatingOsmData.getDuplicatingWay().getWay(), violatingOsmData);
    }

    private boolean hasValidSourceTag(Element element, ViolatingOsmData violatingOsmData) {
        return getSourceTagValue(element)
                .filter(isSourceValueAllowed(violatingOsmData))
                .isPresent();
    }

    private Optional<String> getSourceTagValue(Element osmElement) {
        String sourceValue = osmElement.getTags().get("source");
        return Optional.ofNullable(sourceValue);
    }

    private Predicate<String> isSourceValueAllowed(ViolatingOsmData violatingOsmData) {
        return sourceTagValue -> {
            boolean sourceInConfig = configuredTagValues.contains(sourceTagValue);
            if (!sourceInConfig) {
                log.warn("Source tag value is present but its value: {} is other than configured for violation {}", sourceTagValue, violatingOsmData);
            }
            return sourceInConfig;
        };
    }
}
