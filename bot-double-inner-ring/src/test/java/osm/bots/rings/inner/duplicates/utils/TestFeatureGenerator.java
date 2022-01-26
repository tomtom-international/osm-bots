package osm.bots.rings.inner.duplicates.utils;

import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmRelationMember;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.RelationMember;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class TestFeatureGenerator {

    @Builder(builderMethodName = "wayWithParentRelations")
    public static WayWithParentRelations buildWayWithParentRelations(
            long wayId,
            List<Long> wayNodes,
            Map<String, String> wayTags,
            @NonNull @Singular List<Long> parentRelationIds) {
        List<Relation> parentRelations = parentRelationIds
                .stream()
                .map(relationId -> relation()
                        .id(relationId)
                        .members(List.of(new OsmRelationMember(wayId, "inner", Element.Type.WAY)))
                        .build())
                .collect(Collectors.toList());
        OsmWay wayInViolatingRelation = way()
                .id(wayId)
                .nodes(wayNodes)
                .tags(wayTags)
                .build();
        return new WayWithParentRelations(wayInViolatingRelation, parentRelations);
    }

    @Builder(builderMethodName = "violatingOsmDataWithRelationMember")
    public ViolatingOsmData buildViolatingOsmDataWithRelationMember(
            long relationId,
            WayWithParentRelations innerRingWay,
            WayWithParentRelations duplicatingWay
    ) {
        List<RelationMember> members = new ArrayList<>();
        members.add(new OsmRelationMember(innerRingWay.getWay().getId(), "inner", Element.Type.WAY));

        Relation relation = TestFeatureGenerator.relation()
                .id(relationId)
                .members(members)
                .build();
        return new ViolatingOsmData(relation, innerRingWay, duplicatingWay);
    }

    @Builder(builderMethodName = "relation")
    public static OsmRelation buildRelation(
            long id,
            int version,
            Map<String, String> tags,
            List<RelationMember> members) {
        return new OsmRelation(id, version, getOrDefault(members, new ArrayList<>()), getOrDefault(tags, new HashMap<>()));
    }

    @Builder(builderMethodName = "way")
    public static OsmWay buildWay(
            long id,
            int version,
            List<Long> nodes,
            Map<String, String> tags) {
        return new OsmWay(id, version, getOrDefault(nodes, List.of()), getOrDefault(tags, Map.of()));
    }

    public static <T> T getOrDefault(T value, T defaultValue) {
        return value == null
                ? defaultValue
                : value;
    }
}
