package osm.bots.rings.inner.duplicates.verifiers;

import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

class DuplicatingWayIsNotMemberOfAnyRelationVerifier extends Verifier {

    @Override
    boolean isMatchingVerifierCriteria(ViolatingOsmData violatingOsmData) {
        return violatingOsmData.getDuplicatingWay().getParentRelations().isEmpty();
    }
}
