package osm.bots.rings.inner.duplicates;

import java.util.List;

import static org.assertj.core.condition.AllOf.allOf;
import static osm.bots.rings.inner.duplicates.Assertions.deletedWay;
import static osm.bots.rings.inner.duplicates.Assertions.hasElements;
import static osm.bots.rings.inner.duplicates.Assertions.updatedWayWithTags;
import static osm.bots.rings.inner.duplicates.TestOsmData.SOURCE_TAG;
import static osm.bots.rings.inner.duplicates.TestOsmData.innerWay;
import static osm.bots.rings.inner.duplicates.TestOsmData.wayWithSourceTag;
import static osm.bots.rings.inner.duplicates.TestOsmData.wayWithoutTags;

class ReplaceWayTagsIntegrationTest extends AbstractIntegrationTest {

    private static final List<Long> OVERLAPPING_INNER_WAY_NODES = List.of(1L, 2L, 3L, 1L);
    private static final long WAY_WITH_TAGS_ID = 1L;
    private static final long WAY_IN_RELATION_WITHOUT_TAGS_ID = 2L;
    private static final long RELATION_ID = 1L;

    @Override
    protected List<TestViolation> givenViolations() {

        return List.of(
                TestViolation.of(RELATION_ID, WAY_WITH_TAGS_ID, WAY_IN_RELATION_WITHOUT_TAGS_ID)
        );
    }

    @Override
    protected TestOsmData givenOsmData() {

        return TestOsmData.builder()
                //standard case where relation has a way without tags and there is another way with the same topology and with some tags
                .way(wayWithSourceTag(WAY_WITH_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .way(wayWithoutTags(WAY_IN_RELATION_WITHOUT_TAGS_ID, OVERLAPPING_INNER_WAY_NODES))
                .relation(
                        TestOsmData.relationWithSourceTagBuilder().id(RELATION_ID)
                                .member(innerWay(WAY_IN_RELATION_WITHOUT_TAGS_ID))
                                .build()
                )
                .build();
    }

    @Override
    protected void thenValidateUploadedChangesets(final Assertions.UploadedChangesetsAssert uploadedChangesetsAssert) {

        uploadedChangesetsAssert
                .hasChangesets(1)
                .singleFix().has(
                allOf(
                        deletedWay(WAY_WITH_TAGS_ID),
                        updatedWayWithTags(WAY_IN_RELATION_WITHOUT_TAGS_ID, SOURCE_TAG),
                        hasElements(2)
                )
        );
    }
}
