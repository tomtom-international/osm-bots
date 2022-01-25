package osm.bots.rings.inner.duplicates.verifiers;

import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class WaysHaveMatchingNodesVerifier extends Verifier {

    private static final int MIN_NUMBER_OF_AREAS_NODES = 4;

    @Override
    boolean isMatchingVerifierCriteria(ViolatingOsmData violatingOsmData) {
        List<Long> nodesOfInnerRingWay = violatingOsmData.getNodeIdsOfInnerRingWay();
        List<Long> nodesOfDuplicatingWay = violatingOsmData.getNodeIdsOfDuplicatingWay();

        if (waysHaveDifferentAmountOfNodes(nodesOfInnerRingWay, nodesOfDuplicatingWay)
                || wayIsNotClosed(nodesOfInnerRingWay)
                || wayIsNotClosed(nodesOfDuplicatingWay)) {
            return false;
        }

        List<Long> duplicatedRelationMemberNodes = duplicateWayNodes(nodesOfDuplicatingWay);
        if (hasTheSameNodes(duplicatedRelationMemberNodes, nodesOfInnerRingWay)) {
            return true;
        } else {
            Collections.reverse(duplicatedRelationMemberNodes);
            return hasTheSameNodes(duplicatedRelationMemberNodes, nodesOfInnerRingWay);
        }
    }

    private boolean waysHaveDifferentAmountOfNodes(List<Long> nodesOfInnerRingWay, List<Long> nodesOfDuplicatingWay) {
        return nodesOfInnerRingWay.size() != nodesOfDuplicatingWay.size();
    }

    private boolean hasTheSameNodes(List<Long> duplicatedRelationMemberNodes, List<Long> wayNotBelongingToRelation) {
        int indexOfStartingNode = Collections.indexOfSubList(duplicatedRelationMemberNodes, wayNotBelongingToRelation);
        return indexOfStartingNode != -1;
    }

    private List<Long> duplicateWayNodes(List<Long> wayNodes) {
        List<Long> wayNodesWithoutClosingNode = wayNodes.subList(0, wayNodes.size() - 1);

        List<Long> duplicatedNodes = new ArrayList<>(wayNodesWithoutClosingNode);
        duplicatedNodes.addAll(wayNodesWithoutClosingNode);

        return duplicatedNodes;
    }

    private boolean wayIsNotClosed(List<Long> wayNodes) {
        return wayNodes.size() < MIN_NUMBER_OF_AREAS_NODES || !Objects.equals(wayNodes.get(0), wayNodes.get(wayNodes.size() - 1));
    }
}
