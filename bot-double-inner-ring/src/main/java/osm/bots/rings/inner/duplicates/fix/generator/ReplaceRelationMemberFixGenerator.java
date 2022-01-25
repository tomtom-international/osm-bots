package osm.bots.rings.inner.duplicates.fix.generator;

import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmRelationMember;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.RelationMember;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ReplaceRelationMemberFixGenerator implements FixGenerator {

    private static final String INNER_ROLE = "inner";

    @Override
    public ViolationFix generateFix(ViolatingOsmData violatingOsmData) {
        OsmWay wayWithTags = (OsmWay) violatingOsmData.getDuplicatingWay().getWay();
        OsmWay wayToBeDeleted = (OsmWay) violatingOsmData.getInnerRingWay().getWay();
        OsmRelation relation = (OsmRelation) violatingOsmData.getRelation();
        replaceWayInRelation(relation, wayToBeDeleted.getId(), wayWithTags.getId());
        wayToBeDeleted.setDeleted(true);
        return new ViolationFix(List.of(relation, wayToBeDeleted));
    }

    private void replaceWayInRelation(OsmRelation relation, long wayToBeDeletedId, long wayToBeAddedId) {
        List<RelationMember> members = relation.getMembers();
        OsmRelationMember memberToBeDeleted = (OsmRelationMember) members.stream()
                .filter(member -> isInnerWayWithId(member, wayToBeDeletedId))
                .findFirst()
                .orElseThrow(innerRingNotFoundExceptionSupplier(relation, wayToBeDeletedId));
        OsmRelationMember memberToBeAdded = new OsmRelationMember(wayToBeAddedId, memberToBeDeleted.getRole(), memberToBeDeleted.getType());
        members.remove(memberToBeDeleted);
        members.add(memberToBeAdded);
    }

    private boolean isInnerWayWithId(RelationMember member, long wayId) {
        return member.getRef() == wayId
                && member.getType() == Element.Type.WAY
                && Objects.equals(member.getRole(), INNER_ROLE);
    }

    private Supplier<IllegalStateException> innerRingNotFoundExceptionSupplier(OsmRelation relation, long wayId) {
        return () -> new IllegalStateException(
                String.format("Inner ring way %d cannot be found in relation %d members", wayId, relation.getId()));
    }
}
