package osm.bots.rings.inner.duplicates.osmapi.fix;

import de.westnordost.osmapi.map.data.OsmWay;
import org.junit.jupiter.api.Test;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ViolationFixGeneratorTest {

    @Test
    void shouldGenerateOsmDataChange() {
        //  given
        ViolationFixGenerator violationFixGenerator = new ViolationFixGenerator();
        Map<String, String> tagsExample1 = Map.of("key", "value");
        Map<String, String> tagsExample2 = Map.of("key2", "value2");
        List<Long> nodesExample1 = List.of(1L, 2L, 3L, 1L);
        List<Long> nodesExample2 = List.of(2L, 3L, 1L, 2L);
        ViolatingOsmData violatingOsmData = createViolatingOsmData(nodesExample1, nodesExample2, tagsExample1);
        ViolatingOsmData violatingOsmData2 = createViolatingOsmData(nodesExample2, nodesExample1, tagsExample2);

        //  when
        List<ViolationFix> violationFixes = violationFixGenerator.generateChanges(List.of(violatingOsmData, violatingOsmData2));

        //  then
        ViolationFix expectedChange = createOsmDataChange(nodesExample1, nodesExample2, tagsExample1);
        ViolationFix expectedChange2 = createOsmDataChange(nodesExample2, nodesExample1, tagsExample2);

        assertThat(violationFixes)
                .usingRecursiveComparison()
                .isEqualTo(List.of(expectedChange, expectedChange2));
    }

    private ViolatingOsmData createViolatingOsmData(List<Long> innerWayNodes, List<Long> duplicatingWayNodes, Map<String, String> duplicatingWayTags) {
        return TestFeatureGenerator.violatingOsmData()
                .relationId(1L)
                .innerRingWay(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(1L)
                        .wayTags(new HashMap<>())
                        .wayNodes(innerWayNodes)
                        .parentRelationId(1L)
                        .build())
                .duplicatingWay(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(2L)
                        .wayNodes(duplicatingWayNodes)
                        .wayTags(duplicatingWayTags)
                        .build())
                .build();
    }

    private ViolationFix createOsmDataChange(List<Long> innerWayNodes, List<Long> duplicatingWayNodes, Map<String, String> tags) {
        OsmWay wayWithUpdatedTags = TestFeatureGenerator.way()
                .id(1L)
                .tags(tags)
                .nodes(innerWayNodes)
                .build();
        wayWithUpdatedTags.setModified(true);

        OsmWay wayToRemove = TestFeatureGenerator.way()
                .id(2L)
                .tags(tags)
                .nodes(duplicatingWayNodes)
                .build();
        wayToRemove.setDeleted(true);

        return new ViolationFix(List.of(wayToRemove, wayWithUpdatedTags));
    }
}
