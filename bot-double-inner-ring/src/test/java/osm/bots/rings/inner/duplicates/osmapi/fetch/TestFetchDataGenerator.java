package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmRelationMember;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.RelationMember;
import de.westnordost.osmapi.map.data.Way;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
class TestFetchDataGenerator {

    static OsmData createOsmData(long relationId, long way1Id, long way2id) {
        return osmData()
                .relationId(relationId)
                .wayWithParentRelations(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(way1Id)
                        .parentRelationId(relationId)
                        .build())
                .wayWithParentRelations(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(way2id)
                        .build())
                .build();
    }

    @Builder(builderMethodName = "osmData", builderClassName = "OsmDataBuilder")
    static OsmData buildOsmData(
            long relationId,
            List<RelationMember> relationMembers,
            @Singular("wayWithParentRelations") List<WayWithParentRelations> waysWithParentRelations) {
        List<RelationMember> members = new ArrayList<>();
        long wayId = waysWithParentRelations.get(0).getWay().getId();
        members.add(new OsmRelationMember(wayId, "inner", Element.Type.WAY));
        Relation relation = createRelation(relationId, members);
        return new OsmData(relation, waysWithParentRelations);
    }

    static ViolatingOsmData createViolatingOsmData(long relationId, long way1Id, long way2Id) {
        return TestFeatureGenerator.violatingOsmDataWithRelationMember()
                .relationId(relationId)
                .innerRingWay(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(way1Id)
                        .parentRelationIds(List.of(relationId))
                        .build())
                .duplicatingWay(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(way2Id)
                        .parentRelationIds(List.of())
                        .build())
                .build();
    }

    static OsmRelation createRelation(long relationId, List<RelationMember> members) {
        return TestFeatureGenerator.relation()
                .id(relationId)
                .members(members)
                .build();
    }

    static OsmWay createWay(long WAY_1_ID) {
        return TestFeatureGenerator.way()
                .id(WAY_1_ID)
                .build();
    }
}
