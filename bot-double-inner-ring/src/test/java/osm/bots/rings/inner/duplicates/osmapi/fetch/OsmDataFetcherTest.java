package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.data.Element;
import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmRelationMember;
import de.westnordost.osmapi.map.data.OsmWay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import osm.bots.rings.inner.duplicates.osmose.DuplicatedInnerPolygonViolation;
import osm.bots.rings.inner.duplicates.osmose.ViolatingOsmIds;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OsmDataFetcherTest {

    @Mock
    private OsmDataClient osmDataClient;

    private OsmDataFetcher osmDataFetcher;

    @BeforeEach
    void setUp() {
        osmDataFetcher = new OsmDataFetcher(osmDataClient);
    }

    @Test
    void shouldFetchCorrectData() {
        //  given
        final long WAY_1_ID = 1L;
        final long WAY_2_ID = 2L;
        final long RELATION_ID = 1L;
        OsmWay way1 = TestFetchDataGenerator.createWay(WAY_1_ID);
        OsmWay way2 = TestFetchDataGenerator.createWay(WAY_2_ID);
        OsmRelation relation = TestFetchDataGenerator.createRelation(RELATION_ID, List.of(new OsmRelationMember(way1.getId(), "inner", Element.Type.WAY)));
        when(osmDataClient.getRelation(RELATION_ID)).thenReturn(relation);
        when(osmDataClient.getWay(WAY_1_ID)).thenReturn(way1);
        when(osmDataClient.getWay(WAY_2_ID)).thenReturn(way2);
        when(osmDataClient.getRelationsForWay(WAY_1_ID)).thenReturn(List.of(relation));
        when(osmDataClient.getRelationsForWay(WAY_2_ID)).thenReturn(List.of());

        DuplicatedInnerPolygonViolation violation =
                new DuplicatedInnerPolygonViolation(1170, new ViolatingOsmIds(List.of(RELATION_ID), List.of(WAY_1_ID, WAY_2_ID)));

        //  when
        OsmData actual = osmDataFetcher.fetch(violation);

        // then
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(TestFetchDataGenerator.createOsmData(RELATION_ID, WAY_1_ID, WAY_2_ID));
    }
}
