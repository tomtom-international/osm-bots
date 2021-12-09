package osm.bots.rings.inner.duplicates.verifiers;

import de.westnordost.osmapi.map.data.OsmRelation;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.junit.jupiter.params.provider.Arguments;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.List;
import java.util.Map;

class TestCaseGenerator {

    @Builder(builderMethodName = "sourceVerifierTestCase", builderClassName = "SourceVerifierTestCaseBuilder")
    static Arguments buildSourceVerifierTestCase(
            @NonNull String name,
            @Singular("sourceTagValues") List<String> sourceTagValues,
            @Singular("relationTags") Map<String, String> relationTags,
            @Singular("duplicatingWayTags") Map<String, String> duplicatingWayTags,
            boolean verifierResult) {

        OsmRelation violatingRelation = TestFeatureGenerator.relation()
                .tags(relationTags)
                .build();
        WayWithParentRelations duplicatingWay = TestFeatureGenerator.wayWithParentRelations()
                .wayTags(duplicatingWayTags)
                .build();

        return Arguments.of(
                name,
                sourceTagValues,
                new ViolatingOsmData(
                        violatingRelation,
                        null,
                        duplicatingWay),
                verifierResult);
    }

    @Builder(builderMethodName = "testCase", builderClassName = "TestCaseBuilder")
    static Arguments buildTestCases(
            @NonNull String name,
            long violatingRelationId,
            @Singular("innerRingWayNodes") List<Long> innerRingWayNodes,
            @Singular("innerRingWayTags") Map<String, String> innerRingWayTags,
            @Singular("innerRingWayParentRelations") List<Long> innerRingWayParentRelations,
            @Singular("duplicatingWayNodes") List<Long> duplicatingWayNodes,
            @Singular("duplicatingWayTags") Map<String, String> duplicatingWayTags,
            @Singular("duplicatingWayParentRelations") List<Long> duplicatingWayParentRelations,
            boolean verifierResult) {

        OsmRelation violatingRelation = TestFeatureGenerator.relation()
                .id(violatingRelationId)
                .build();
        WayWithParentRelations innerRingWay = TestFeatureGenerator.wayWithParentRelations()
                .wayNodes(innerRingWayNodes)
                .wayTags(innerRingWayTags)
                .parentRelationIds(innerRingWayParentRelations)
                .build();
        WayWithParentRelations duplicatingWay = TestFeatureGenerator.wayWithParentRelations()
                .wayNodes(duplicatingWayNodes)
                .wayTags(duplicatingWayTags)
                .parentRelationIds(duplicatingWayParentRelations)
                .build();

        return Arguments.of(
                name,
                new ViolatingOsmData(
                        violatingRelation,
                        innerRingWay,
                        duplicatingWay),
                verifierResult);
    }
}
