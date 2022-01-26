package osm.bots.rings.inner.duplicates;

import java.util.List;

import static osm.bots.rings.inner.duplicates.Assertions.UploadedChangesetsAssert;
import static osm.bots.rings.inner.duplicates.TestOsmData.innerWay;
import static osm.bots.rings.inner.duplicates.TestOsmData.wayWithSourceTag;
import static osm.bots.rings.inner.duplicates.TestOsmData.wayWithoutTags;

class DoNotGenerateFixIntegrationTest extends AbstractIntegrationTest {

    private static final List<Long> OVERLAPPING_INNER_WAY_NODES = List.of(1L, 2L, 3L, 1L);
    private static final List<Long> DIFFERENT_INNER_WAY_NODES = List.of(1L, 2L, 4L, 1L);
    private static final List<Long> NOT_CLOSED_NODES = List.of(1L, 2L, 3L);
    private static final long REPEATING_RELATION_1_ID = 1L;
    private static final long RELATION_WITHOUT_SOURCE_TAG_2_ID = 2L;
    private static final long RELATION_3_ID = 3L;
    private static final long RELATION_4_ID = 4L;
    private static final long RELATION_5_ID = 5L;
    private static final long WAY_1_WITH_TAGS_ID = 1L;
    private static final long WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID = 2L;
    private static final long WAY_3_IN_RELATION_1_WITHOUT_TAGS_ID = 3L;
    private static final long WAY_4_WITH_TAGS_ID = 4L;
    private static final long WAY_5_IN_RELATION_2_WITH_TAGS_ID = 5L;
    private static final long WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID = 6L;
    private static final long WAY_7_WITHOUT_TAGS_ID = 7L;
    private static final long WAY_8_IN_RELATION_4_WITH_NOT_CLOSED_GEOMETRY_ID = 8L;
    private static final long WAY_9_WITH_TAGS_ID = 9L;
    private static final long WAY_10_IN_RELATION_5_WITH_DIFFERENT_GEOMETRY_ID = 10L;
    private static final long WAY_11_WITH_TAGS_ID = 11L;

    @Override
    protected List<TestViolation> givenViolations() {

        return List.of(
                //case with way repeating many times in violations for one relation - should be skipped
                TestViolation.of(REPEATING_RELATION_1_ID, WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID, WAY_1_WITH_TAGS_ID),
                TestViolation.of(REPEATING_RELATION_1_ID, WAY_3_IN_RELATION_1_WITHOUT_TAGS_ID, WAY_1_WITH_TAGS_ID),
                //case where both ways have tags - should be skipped
                TestViolation.of(RELATION_WITHOUT_SOURCE_TAG_2_ID, WAY_5_IN_RELATION_2_WITH_TAGS_ID, WAY_4_WITH_TAGS_ID),
                //case where ways have no source tags - should be skipped
                TestViolation.of(RELATION_3_ID, WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID, WAY_7_WITHOUT_TAGS_ID),
                //case where inner ring is not closed - should be skipped
                TestViolation.of(RELATION_4_ID, WAY_8_IN_RELATION_4_WITH_NOT_CLOSED_GEOMETRY_ID, WAY_9_WITH_TAGS_ID),
                //case where way's geometry doesn't match - should be skipped
                TestViolation.of(RELATION_5_ID, WAY_10_IN_RELATION_5_WITH_DIFFERENT_GEOMETRY_ID, WAY_11_WITH_TAGS_ID)
        );
    }

    @Override
    protected TestOsmData givenOsmData() {

        return TestOsmData.builder()

                .way(wayWithSourceTag(WAY_1_WITH_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .way(wayWithoutTags(WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .way(wayWithoutTags(WAY_3_IN_RELATION_1_WITHOUT_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .way(wayWithSourceTag(WAY_4_WITH_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .way(wayWithSourceTag(WAY_5_IN_RELATION_2_WITH_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .way(wayWithoutTags(WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .way(wayWithoutTags(WAY_7_WITHOUT_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .way(wayWithSourceTag(WAY_8_IN_RELATION_4_WITH_NOT_CLOSED_GEOMETRY_ID, NOT_CLOSED_NODES))
                .way(wayWithoutTags(WAY_9_WITH_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .way(wayWithSourceTag(WAY_10_IN_RELATION_5_WITH_DIFFERENT_GEOMETRY_ID, DIFFERENT_INNER_WAY_NODES))
                .way(wayWithoutTags(WAY_11_WITH_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .relation(
                        TestOsmData.relationWithSourceTagBuilder().id(REPEATING_RELATION_1_ID)
                                .member(innerWay(WAY_2_IN_RELATION_1_WITHOUT_TAGS_ID))
                                .member(innerWay(WAY_3_IN_RELATION_1_WITHOUT_TAGS_ID))
                                .build())
                .relation(
                        TestOsmData.relationWithoutTagsBuilder().id(RELATION_WITHOUT_SOURCE_TAG_2_ID)
                                .member(innerWay(WAY_5_IN_RELATION_2_WITH_TAGS_ID))
                                .build())
                .relation(
                        TestOsmData.relationWithSourceTagBuilder().id(RELATION_3_ID)
                                .member(innerWay(WAY_6_IN_RELATION_3_WITHOUT_TAGS_ID))
                                .build())
                .relation(
                        TestOsmData.relationWithSourceTagBuilder().id(RELATION_4_ID)
                                .member(innerWay(WAY_8_IN_RELATION_4_WITH_NOT_CLOSED_GEOMETRY_ID))
                                .build())
                .relation(
                        TestOsmData.relationWithSourceTagBuilder().id(RELATION_5_ID)
                                .member(innerWay(WAY_10_IN_RELATION_5_WITH_DIFFERENT_GEOMETRY_ID))
                                .build())
                .build();
    }

    @Override
    protected void thenValidateUploadedChangesets(final UploadedChangesetsAssert uploadedChangesetsAssert) {

        uploadedChangesetsAssert.hasChangesets(0);

    }
}
