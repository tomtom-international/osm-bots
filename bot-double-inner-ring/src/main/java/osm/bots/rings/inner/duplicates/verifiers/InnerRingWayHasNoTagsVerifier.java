package osm.bots.rings.inner.duplicates.verifiers;

import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

class InnerRingWayHasNoTagsVerifier extends Verifier {

    @Override
    boolean isMatchingVerifierCriteria(ViolatingOsmData violatingOsmData) {
        return violatingOsmData
                .getInnerRingWay()
                .getWay()
                .getTags()
                .isEmpty();
    }
}
