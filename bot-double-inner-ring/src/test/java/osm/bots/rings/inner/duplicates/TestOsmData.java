package osm.bots.rings.inner.duplicates;

import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmRelationMember;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.RelationMember;
import de.westnordost.osmapi.map.data.Way;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Builder
@Getter
class TestOsmData {

    private static final int INITIAL_VERSION = 1;
    private static final Map<String, String> ANY_TAGS = Map.of("any_key", "any value");
    private static final String INNER_ROLE = "inner";
    public static final Map<String, String> SOURCE_TAG = Map.of("source", "TEST");

    @Singular
    private final List<Way> ways;
    @Singular
    private final List<Relation> relations;

    Map<Long, List<Relation>> getWaysRelations() {
        return relations.stream().flatMap(this::relationWayMembers)
                .collect(groupingBy(member -> member.relationMember.getRef(),
                        mapping(RelationMemberWithRelation::getRelation, toList())));
    }

    static OsmWay wayWithoutTags(final long id, final List<Long> nodes) {
        return new OsmWay(id, INITIAL_VERSION, new ArrayList<>(nodes), null);
    }

    static OsmWay wayWithSourceTag(final long id, final List<Long> nodes) {
        return new OsmWay(id, INITIAL_VERSION, new ArrayList<>(nodes), SOURCE_TAG);
    }

    static OsmRelationMember innerWay(final long wayId) {
        return new OsmRelationMember(wayId, INNER_ROLE, Element.Type.WAY);
    }

    @Builder(builderMethodName = "relationWithSourceTagBuilder", builderClassName = "RelationWithSourceTagBuilder")
    static OsmRelation relationWithSourceTag(final long id, @Singular final List<RelationMember> members) {
        return new OsmRelation(id, INITIAL_VERSION, new ArrayList<>(members), SOURCE_TAG);
    }

    @Builder(builderMethodName = "relationWithoutTagsBuilder", builderClassName = "RelationWithoutTagsBuilder")
    static OsmRelation relationWithoutTags(final long id, @Singular final List<RelationMember> members) {
        return new OsmRelation(id, INITIAL_VERSION, new ArrayList<>(members), null);
    }

    private Stream<RelationMemberWithRelation> relationWayMembers(final Relation relation) {
        return relation.getMembers().stream()
                .filter(member -> member.getType() == Element.Type.WAY)
                .map(member -> new RelationMemberWithRelation(member, relation));
    }

    @Value
    private static class RelationMemberWithRelation {

        RelationMember relationMember;
        Relation relation;
    }
}
