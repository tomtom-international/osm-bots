package osm.bots.rings.inner.duplicates.verifiers;

import de.westnordost.osmapi.map.data.Element;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.Collection;
import java.util.Optional;

@Slf4j
class SourceVerifier extends Verifier {

    private final Collection<String> configuredTagValues;

    SourceVerifier(Collection<String> tagValues) {
        this.configuredTagValues = tagValues;
    }

    @Override
    boolean isMatchingVerifierCriteria(ViolatingOsmData violatingOsmData) {
        Optional<String> relationSourceTagValue = getSourceTagValue(violatingOsmData.getRelation());
        Optional<String> duplicatedWaySourceTagValue = getSourceTagValue(violatingOsmData.getDuplicatingWay().getWay());
        if (relationSourceTagValue.isEmpty()) {
            return false;
        }
        if (relationSourceTagValue.equals(duplicatedWaySourceTagValue)) {
            return isSourceValueAllowed(relationSourceTagValue.get(), violatingOsmData);
        } else {
            log.warn("Different Source tag value between duplicating way: {} and relation: {} for violation {}",
                    relationSourceTagValue.get(), duplicatedWaySourceTagValue.orElse(""), violatingOsmData);
            return false;
        }
    }

    private boolean isSourceValueAllowed(String relationSourceTagValue, ViolatingOsmData violatingOsmData) {
        if (configuredTagValues.contains(relationSourceTagValue)) {
            return true;
        } else {
            log.warn("Source tag value is present but its value: {} is other than configured for violation {}", relationSourceTagValue, violatingOsmData);
            return false;
        }
    }

    private Optional<String> getSourceTagValue(Element osmElement) {
        String sourceValue = osmElement.getTags().get("source");
        return Optional.ofNullable(sourceValue);
    }
}
