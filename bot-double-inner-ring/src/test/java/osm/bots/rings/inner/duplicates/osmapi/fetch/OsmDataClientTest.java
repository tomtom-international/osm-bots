package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.common.errors.OsmConnectionException;
import de.westnordost.osmapi.map.MapDataApi;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OsmDataClientTest {

    private static final long FEATURE_ID = 10633691L;

    @Mock
    private MapDataApi mapDataApi;

    private OsmDataClient osmDataClient;

    @BeforeEach
    void setUp() {
        osmDataClient = new OsmDataClient(mapDataApi);
    }

    @Test
    void shouldRetryGettingWayOnOsmConnectionException() {
        //  given
        Way way = mock(OsmWay.class);
        when(mapDataApi.getWay(FEATURE_ID))
                .thenThrow(OsmConnectionException.class)
                .thenReturn(way);

        //  when
        Way actualWay = osmDataClient.getWay(FEATURE_ID);

        //  then
        verify(mapDataApi, times(2))
                .getWay(FEATURE_ID);
        assertThat(actualWay).isSameAs(way);
    }

    @Test
    void shouldRetryGettingRelationOnOsmConnectionException() {
        //  given
        Relation relation = mock(Relation.class);
        when(mapDataApi.getRelation(FEATURE_ID))
                .thenThrow(OsmConnectionException.class)
                .thenReturn(relation);

        //  when
        Relation actualRelation = osmDataClient.getRelation(FEATURE_ID);

        //  then
        verify(mapDataApi, times(2))
                .getRelation(FEATURE_ID);
        assertThat(actualRelation).isSameAs(relation);
    }

    @Test
    void shouldRetryGettingRelationsOnOsmConnectionException() {
        //  given
        when(mapDataApi.getRelationsForWay(FEATURE_ID))
                .thenThrow(OsmConnectionException.class)
                .thenReturn(List.of());

        //  when
        List<Relation> actualRelationsForWay = osmDataClient.getRelationsForWay(FEATURE_ID);

        //  then
        verify(mapDataApi, times(2))
                .getRelationsForWay(FEATURE_ID);
        assertThat(actualRelationsForWay).isEmpty();
    }
}
