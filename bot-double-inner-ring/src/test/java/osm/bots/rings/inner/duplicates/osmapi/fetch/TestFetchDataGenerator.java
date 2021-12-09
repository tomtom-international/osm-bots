package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.Relation;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

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
            @Singular("wayWithParentRelations") List<WayWithParentRelations> waysWithParentRelations) {
        Relation relation = createRelation(relationId);
        return new OsmData(relation, waysWithParentRelations);
    }

    static ViolatingOsmData createViolatingOsmData(long relationId, long way1Id, long way2Id) {
        return TestFeatureGenerator.violatingOsmData()
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

    static OsmRelation createRelation(long relationId) {
        return TestFeatureGenerator.relation()
                .id(relationId)
                .build();
    }

    static OsmWay createWay(long WAY_1_ID) {
        return TestFeatureGenerator.way()
                .id(WAY_1_ID)
                .build();
    }
}
