package osm.bots.rings.inner.duplicates;

import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.RelationMember;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Condition;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

class Assertions {

    public static UploadedChangesetsAssert assertThat(final List<List<ViolationFix>> uploadedChangesets) {
        return new UploadedChangesetsAssert(uploadedChangesets);
    }

    public static Condition<ViolationFix> deletedWay(final long wayId) {
        return new Condition<>(
                fix -> wayWithId(wayId, fix)
                        .map(Element::isDeleted)
                        .orElse(false),
                String.format("Fix should contain deleted way with ID %d", wayId));
    }

    public static Condition<ViolationFix> updatedWayWithTags(final long wayId, final Map<String, String> tags) {
        return new Condition<>(
                fix -> wayWithId(wayId, fix)
                        .filter(OsmWay::isModified)
                        .map(way -> Objects.equals(way.getTags(), tags))
                        .orElse(false),
                String.format("Fix should contain updated way with ID %d and tags %s", wayId, tags));
    }

    public static Condition<ViolationFix> hasElements(int numberOfElements) {
        return new Condition<>(
                fix -> fix.getEdits().size() == numberOfElements
                , String.format("Fix should contain %d edited elements", numberOfElements)
        );
    }

    public static Condition<ViolationFix> updatedRelationMember(long relationId, long replacedWayId, long newWayId) {
        return new Condition<>(
                fix -> relationWithId(relationId, fix)
                        .map(
                                relation -> relationWayReference(replacedWayId, relation.getMembers()).isEmpty() &&
                                        relationWayReference(newWayId, relation.getMembers()).isPresent()
                        ).isPresent()
                , String.format("Fix should contain relation with reference to way %d and no reference to %d", newWayId, replacedWayId)
        );
    }

    private static Optional<RelationMember> relationWayReference(long id, List<RelationMember> members) {

        return members.stream()
                .filter(member -> member.getRef() == id)
                .filter(member -> member.getType() == Element.Type.WAY)
                .findFirst();
    }

    private static Optional<OsmRelation> relationWithId(long relationId, ViolationFix fix) {
        return elementWithId(relationId, Element.Type.RELATION, fix).map(element -> (OsmRelation) element);
    }

    private static Optional<OsmWay> wayWithId(long wayId, ViolationFix fix) {
        return elementWithId(wayId, Element.Type.WAY, fix).map(element -> (OsmWay) element);
    }

    private static Optional<Element> elementWithId(long elementId, Element.Type type, ViolationFix fix) {
        return fix.getEdits().stream()
                .filter(edit -> edit.getId() == elementId)
                .filter(edit -> edit.getType() == type)
                .findFirst();
    }

    static class UploadedChangesetsAssert extends AbstractAssert<UploadedChangesetsAssert, List<List<ViolationFix>>> {

        protected UploadedChangesetsAssert(final List<List<ViolationFix>> uploadedChangesets) {
            super(uploadedChangesets, UploadedChangesetsAssert.class);
        }

        public ObjectAssert<ViolationFix> singleFix() {
            return org.assertj.core.api.Assertions.assertThat(actual.stream().flatMap(Collection::stream))
                    .singleElement();
        }

        public ListAssert<ViolationFix> fixes() {
            return org.assertj.core.api.Assertions.assertThat(
                    actual.stream().flatMap(Collection::stream).collect(Collectors.toList()));
        }

        public UploadedChangesetsAssert hasChangesets(final int number) {
            org.assertj.core.api.Assertions.assertThat(actual).hasSize(number);
            return this;
        }

        public UploadedChangesetsAssert changesetHasFixes(final int changesetIndex, int numberOfFixes) {
            org.assertj.core.api.Assertions.assertThat(actual.get(changesetIndex)).hasSize(numberOfFixes);
            return this;
        }
    }
}
