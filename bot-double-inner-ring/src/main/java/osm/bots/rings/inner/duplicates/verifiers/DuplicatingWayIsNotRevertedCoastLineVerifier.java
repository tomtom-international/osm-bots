package osm.bots.rings.inner.duplicates.verifiers;

import de.westnordost.osmapi.map.data.Element;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

final class DuplicatingWayIsNotRevertedCoastLineVerifier extends Verifier {

    private static final String COAST_LINE_TAG_KEY = "natural";
    private static final String COAST_LINE_TAG_VALUE = "coastline";

    @Override
    boolean isMatchingVerifierCriteria(ViolatingOsmData violatingOsmData) {
        WayWithParentRelations duplicatingWay = violatingOsmData.getDuplicatingWay();
        boolean hasCoastLineTag = getNaturalTagValue(duplicatingWay.getWay())
                .filter(isCoastline())
                .isPresent();
        if (hasCoastLineTag) {
            List<Long> duplicatingWayNodeIds = duplicatingWay.getNodesOfWay();
            List<Long> innerRingNodes = violatingOsmData.getInnerRingWay().getNodesOfWay();
            return innerRingNodes.equals(duplicatingWayNodeIds);
        }
        return true;
    }

    private Optional<String> getNaturalTagValue(Element element) {
        String naturalTagValue = element
                .getTags()
                .get(COAST_LINE_TAG_KEY);
        return Optional.ofNullable(naturalTagValue);
    }

    private Predicate<String> isCoastline() {
        return tagValue -> tagValue.equalsIgnoreCase(COAST_LINE_TAG_VALUE);
    }
}

