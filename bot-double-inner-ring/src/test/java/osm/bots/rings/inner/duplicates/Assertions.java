package osm.bots.rings.inner.duplicates;

import de.westnordost.osmapi.map.data.Element;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Condition;
import org.assertj.core.api.ObjectAssert;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

class Assertions {

    public static UploadedChangesetsAssert assertThat(final List<List<ViolationFix>> uploadedChangesets) {
        return new UploadedChangesetsAssert(uploadedChangesets);
    }

    public static Condition<ViolationFix> deletedWay(final long wayId) {
        return new Condition<>(
                fix -> wayWithId(wayId, fix)
                        .map(Element::isDeleted)
                        .orElse(false),
                "Fix should contain deleted way with ID " + wayId);
    }

    public static Condition<ViolationFix> updatedWayWithTags(final long wayId, final Map<String, String> tags) {
        return new Condition<>(
                fix -> wayWithId(wayId, fix)
                        .map(way -> Objects.equals(way.getTags(), tags))
                        .orElse(false),
                "Fix should contain way with ID " + wayId + " and tags " + tags);
    }

    public static Condition<ViolationFix> hasNoRelation() {
        return new Condition<>(
                fix -> fix.getEdits().stream()
                        .filter(element -> element.getType() == Element.Type.RELATION)
                        .findAny()
                        .isEmpty()
                , "Fix should not contain relations"
        );
    }

    private static Optional<Element> wayWithId(final long wayId, final ViolationFix fix) {
        return fix.getEdits().stream()
                .filter(edit -> edit.getId() == wayId)
                .filter(edit -> edit.getType() == Element.Type.WAY)
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

        public UploadedChangesetsAssert hasChangesets(final int number) {
            org.assertj.core.api.Assertions.assertThat(actual).hasSize(number);
            return this;
        }
    }
}
