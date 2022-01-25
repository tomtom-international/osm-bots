package osm.bots.rings.inner.duplicates.osmapi.fix;

import de.westnordost.osmapi.map.data.OsmWay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static osm.bots.rings.inner.duplicates.osmapi.fix.FixGeneratorTestData.NODES_EXAMPLE;

class ReplaceWayTagsFixGeneratorTest {

    private static final Map<String, String> TAGS_EXAMPLE = Map.of("key", "value");

    private ReplaceWayTagsFixGenerator replaceWayTagsFixGenerator;

    @BeforeEach
    void setUp() {
        replaceWayTagsFixGenerator = new ReplaceWayTagsFixGenerator();
    }

    @Test
    void shouldGenerateOsmDataChange() {
        //  given
        ViolatingOsmData violatingOsmData = FixGeneratorTestData.createViolatingOsmDataWithRelationMember();

        //  when
        List<ViolationFix> violationFixes = replaceWayTagsFixGenerator.generateFixes(List.of(violatingOsmData));

        //  then
        ViolationFix expectedChange = createExpectedChangeForWays();
        assertThat(violationFixes)
                .usingRecursiveComparison()
                .isEqualTo(List.of(expectedChange));
    }

    private ViolationFix createExpectedChangeForWays() {
        OsmWay wayWithUpdatedTags = TestFeatureGenerator.way()
                .id(1L)
                .tags(ReplaceWayTagsFixGeneratorTest.TAGS_EXAMPLE)
                .nodes(NODES_EXAMPLE)
                .build();
        wayWithUpdatedTags.setModified(true);

        OsmWay wayToRemove = TestFeatureGenerator.way()
                .id(2L)
                .tags(ReplaceWayTagsFixGeneratorTest.TAGS_EXAMPLE)
                .nodes(NODES_EXAMPLE)
                .build();
        wayToRemove.setDeleted(true);

        return new ViolationFix(List.of(wayToRemove, wayWithUpdatedTags));
    }
}
