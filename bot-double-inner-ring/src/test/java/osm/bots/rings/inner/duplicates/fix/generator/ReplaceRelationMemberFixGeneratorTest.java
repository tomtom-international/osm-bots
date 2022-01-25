package osm.bots.rings.inner.duplicates.fix.generator;

import de.westnordost.osmapi.map.data.Element.Type;
import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmRelationMember;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.RelationMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static osm.bots.rings.inner.duplicates.fix.generator.FixGeneratorTestData.NODES_EXAMPLE;

class ReplaceRelationMemberFixGeneratorTest {

    private static final String INNER_ROLE = "inner";
    private static final OsmRelationMember EXAMPLE_MEMBER = new OsmRelationMember(2L, INNER_ROLE, Type.WAY);

    private ReplaceRelationMemberFixGenerator replaceRelationMemberFixGenerator;

    @BeforeEach
    void setUp() {
        replaceRelationMemberFixGenerator = new ReplaceRelationMemberFixGenerator();
    }

    @Test
    void shouldGenerateFixForReplacingRelationMembers() {
        //  given
        ViolatingOsmData violatingOsmData = FixGeneratorTestData.createViolatingOsmDataWithRelationMember();

        //  when
        List<ViolationFix> violationFixes = replaceRelationMemberFixGenerator.generateFixes(List.of(violatingOsmData));

        //  then
        assertThat(violationFixes)
                .usingRecursiveComparison()
                .isEqualTo(createExpectedChangesForWayAndRelation());
    }

    @Test
    void shouldThrowExceptionIfMemberHasNotBeenFound() {
        //  given
        List<ViolatingOsmData> violatingOsmDataWithoutRelationMember = List.of(createViolatingOsmDataWithoutRelationMember());

        //  then
        assertThatThrownBy(() -> replaceRelationMemberFixGenerator.generateFixes(violatingOsmDataWithoutRelationMember))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldModifyRelationWhenMemberIsRemoved() {
        //  given
        OsmRelation relation = createRelationWithMember(EXAMPLE_MEMBER);
        assertThat(relation.isModified()).isFalse();

        //  when
        relation.getMembers().remove(0);

        //  then
        assertThat(relation.isModified()).isTrue();
        assertThat(relation.getMembers()).isEmpty();
    }

    @Test
    void shouldModifyRelationWhenMemberIsAdded() {
        //  given
        OsmRelation relation = createRelationWithMember(EXAMPLE_MEMBER);
        assertThat(relation.isModified()).isFalse();

        //  when
        OsmRelationMember newMember = new OsmRelationMember(3L, INNER_ROLE, Type.WAY);
        relation.getMembers().add(newMember);

        //  then
        assertThat(relation.isModified()).isTrue();
        assertThat(relation.getMembers()).containsExactly(EXAMPLE_MEMBER, newMember);
    }

    private ViolatingOsmData createViolatingOsmDataWithoutRelationMember() {
        ViolatingOsmData violatingData = TestFeatureGenerator.violatingOsmDataWithRelationMember()
                .relationId(1L)
                .innerRingWay(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(1L)
                        .wayTags(new HashMap<>())
                        .wayNodes(NODES_EXAMPLE)
                        .build())
                .duplicatingWay(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(2L)
                        .wayNodes(NODES_EXAMPLE)
                        .build())
                .build();
        violatingData.getRelation().getMembers().remove(0);
        return violatingData;
    }

    private List<ViolationFix> createExpectedChangesForWayAndRelation() {
        OsmWay wayToBeDeleted = TestFeatureGenerator.way()
                .id(1L)
                .nodes(NODES_EXAMPLE)
                .build();
        wayToBeDeleted.setDeleted(true);
        OsmWay wayToReplaceRelationMember = TestFeatureGenerator.way()
                .id(2L)
                .nodes(NODES_EXAMPLE)
                .build();
        List<RelationMember> members = new ArrayList<>();
        members.add(new OsmRelationMember(wayToReplaceRelationMember.getId(), INNER_ROLE, Type.WAY));
        OsmRelation editedRelation = TestFeatureGenerator.relation()
                .id(1L)
                .members(members)
                .build();
        editedRelation.setModified(true);
        return List.of(new ViolationFix(List.of(editedRelation, wayToBeDeleted)));
    }

    private OsmRelation createRelationWithMember(RelationMember member) {
        List<RelationMember> members = new ArrayList<>();
        members.add(member);
        return new OsmRelation(1L, 1, members, new HashMap<>());
    }
}
