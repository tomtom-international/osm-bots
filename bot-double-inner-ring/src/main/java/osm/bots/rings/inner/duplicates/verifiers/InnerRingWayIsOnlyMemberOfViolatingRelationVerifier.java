package osm.bots.rings.inner.duplicates.verifiers;

import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.List;

class InnerRingWayIsOnlyMemberOfViolatingRelationVerifier extends Verifier {

    @Override
    boolean isMatchingVerifierCriteria(ViolatingOsmData violatingOsmData) {
        List<Long> idsOfAllParentRelations = violatingOsmData.getInnerRingWay().getIdsOfParentRelations();
        return idsOfAllParentRelations.equals(List.of(violatingOsmData.getRelation().getId()));
    }
}
