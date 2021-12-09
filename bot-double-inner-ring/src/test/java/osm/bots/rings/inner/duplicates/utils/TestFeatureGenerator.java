package osm.bots.rings.inner.duplicates.utils;

import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.RelationMember;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;

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
                        .build())
                .collect(Collectors.toList());
        OsmWay wayInViolatingRelation = way()
                .id(wayId)
                .nodes(wayNodes)
                .tags(wayTags)
                .build();
        return new WayWithParentRelations(wayInViolatingRelation, parentRelations);
    }

    @Builder(builderMethodName = "relation")
    public static OsmRelation buildRelation(
            long id,
            int version,
            Map<String, String> tags,
            @Singular("member") List<RelationMember> members) {
        return new OsmRelation(id, version, getOrDefault(members, List.of()), getOrDefault(tags, Map.of()));
    }

    @Builder(builderMethodName = "way")
    public static OsmWay buildWay(
            long id,
            int version,
            List<Long> nodes,
            Map<String, String> tags) {
        return new OsmWay(id, version, getOrDefault(nodes, List.of()), getOrDefault(tags, Map.of()));
    }

    @Builder(builderMethodName = "violatingOsmData")
    public ViolatingOsmData buildViolatingOsmData(
            long relationId,
            WayWithParentRelations innerRingWay,
            WayWithParentRelations duplicatingWay
    ) {
        Relation relation = TestFeatureGenerator.relation()
                .id(relationId)
                .build();

        return new ViolatingOsmData(relation, innerRingWay, duplicatingWay);
    }

    public static <T> T getOrDefault(T value, T defaultValue) {
        return value == null
                ? defaultValue
                : value;
    }
}
