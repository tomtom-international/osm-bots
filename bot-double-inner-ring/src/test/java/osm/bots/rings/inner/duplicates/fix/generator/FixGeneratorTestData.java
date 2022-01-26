package osm.bots.rings.inner.duplicates.fix.generator;

import lombok.experimental.UtilityClass;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
class FixGeneratorTestData {

    static final List<Long> NODES_EXAMPLE = List.of(1L, 2L, 3L, 1L);

    static ViolatingOsmData createViolatingOsmDataWithRelationMember() {
        return TestFeatureGenerator.violatingOsmDataWithRelationMember()
                .relationId(1L)
                .innerRingWay(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(1L)
                        .wayTags(new HashMap<>())
                        .wayNodes(NODES_EXAMPLE)
                        .parentRelationId(1L)
                        .build())
                .duplicatingWay(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(2L)
                        .wayNodes(NODES_EXAMPLE)
                        .wayTags(Map.of("key", "value"))
                        .build())
                .build();
    }
}
