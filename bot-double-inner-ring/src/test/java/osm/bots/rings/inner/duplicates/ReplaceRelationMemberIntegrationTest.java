package osm.bots.rings.inner.duplicates;

import java.util.List;

import static org.assertj.core.api.Assertions.allOf;
import static osm.bots.rings.inner.duplicates.Assertions.UploadedChangesetsAssert;
import static osm.bots.rings.inner.duplicates.Assertions.deletedWay;
import static osm.bots.rings.inner.duplicates.Assertions.hasElements;
import static osm.bots.rings.inner.duplicates.Assertions.updatedRelationMember;
import static osm.bots.rings.inner.duplicates.TestOsmData.innerWay;
import static osm.bots.rings.inner.duplicates.TestOsmData.wayWithSourceTag;
import static osm.bots.rings.inner.duplicates.TestOsmData.wayWithoutTags;

class ReplaceRelationMemberIntegrationTest extends AbstractIntegrationTest {

    private static final List<Long> OVERLAPPING_INNER_WAY_NODES_1 = List.of(1L, 2L, 3L, 1L);
    private static final List<Long> OVERLAPPING_INNER_WAY_NODES_2 = List.of(4L, 5L, 6L, 4L);
    private static final long RELATION_1_ID = 1L;
    private static final long RELATION_2_ID = 2L;
    private static final long RELATION_3_ID = 3L;
    private static final long WAY_1_WITH_TAGS_ID = 1L;
    private static final long WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID = 2L;
    private static final long WAY_3_IN_RELATION_2_WITHOUT_TAGS_ID = 3L;
    private static final long WAY_4_WITH_TAGS_ID = 4L;
    private static final long WAY_5_IN_RELATION_2_WITHOUT_TAGS_ID = 5L;
    private static final long WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID = 6L;

    @Override
    protected List<TestViolation> givenViolations() {

        return List.of(
                //standard case with two relations that have duplicated inner ways
                TestViolation.of(RELATION_1_ID, WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID, WAY_1_WITH_TAGS_ID),
                TestViolation.of(RELATION_2_ID, WAY_1_WITH_TAGS_ID, WAY_3_IN_RELATION_2_WITHOUT_TAGS_ID),//no assumptions about way's order provided by Osmium
                //case where relation 2 has many duplicated inner rings - like lake with many duplicated islands
                TestViolation.of(RELATION_2_ID, WAY_5_IN_RELATION_2_WITHOUT_TAGS_ID, WAY_4_WITH_TAGS_ID),
                TestViolation.of(RELATION_3_ID, WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID, WAY_4_WITH_TAGS_ID)
        );
    }

    @Override
    protected TestOsmData givenOsmData() {

        return TestOsmData.builder()

                .way(wayWithSourceTag(WAY_1_WITH_TAGS_ID, OVERLAPPING_INNER_WAY_NODES_1))
                .way(wayWithoutTags(WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID, OVERLAPPING_INNER_WAY_NODES_1))
                .way(wayWithoutTags(WAY_3_IN_RELATION_2_WITHOUT_TAGS_ID, OVERLAPPING_INNER_WAY_NODES_1))
                .way(wayWithSourceTag(WAY_4_WITH_TAGS_ID, OVERLAPPING_INNER_WAY_NODES_2))
                .way(wayWithoutTags(WAY_5_IN_RELATION_2_WITHOUT_TAGS_ID, OVERLAPPING_INNER_WAY_NODES_2))
                .way(wayWithoutTags(WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID, OVERLAPPING_INNER_WAY_NODES_2))
                .relation(
                        TestOsmData.relationWithSourceTagBuilder().id(RELATION_1_ID)
                                .member(innerWay(WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID))
                                .build())
                .relation(
                        TestOsmData.relationWithSourceTagBuilder().id(RELATION_2_ID)
                                .member(innerWay(WAY_3_IN_RELATION_2_WITHOUT_TAGS_ID))
                                .member(innerWay(WAY_5_IN_RELATION_2_WITHOUT_TAGS_ID))
                                .build())
                .relation(
                        TestOsmData.relationWithSourceTagBuilder().id(RELATION_3_ID)
                                .member(innerWay(WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID))
                                .build())
                .build();
    }

    @Override
    protected void thenValidateUploadedChangesets(final UploadedChangesetsAssert uploadedChangesetsAssert) {

        uploadedChangesetsAssert
                .hasChangesets(2)
                .changesetHasFixes(0, 2)
                .changesetHasFixes(1, 2)
                .fixes()
                .areExactly(1, allOf(
                        deletedWay(WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID),
                        updatedRelationMember(RELATION_1_ID, WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID, WAY_1_WITH_TAGS_ID),
                        hasElements(2)))
                .areExactly(1, allOf(
                        deletedWay(WAY_3_IN_RELATION_2_WITHOUT_TAGS_ID),
                        updatedRelationMember(RELATION_2_ID, WAY_3_IN_RELATION_2_WITHOUT_TAGS_ID, WAY_1_WITH_TAGS_ID),
                        hasElements(2)))
                .areExactly(1, allOf(
                        deletedWay(WAY_5_IN_RELATION_2_WITHOUT_TAGS_ID),
                        updatedRelationMember(RELATION_2_ID, WAY_5_IN_RELATION_2_WITHOUT_TAGS_ID, WAY_4_WITH_TAGS_ID),
                        hasElements(2)))
                .areExactly(1, allOf(
                        deletedWay(WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID),
                        updatedRelationMember(RELATION_3_ID, WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID, WAY_4_WITH_TAGS_ID),
                        hasElements(2)));

    }
}
