package osm.bots.rings.inner.duplicates.osmapi.store;

import de.westnordost.osmapi.common.errors.OsmConnectionException;
import de.westnordost.osmapi.map.MapDataApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OsmWriteClientTest {

    final static long DEFAULT_ID = 10633691L;

    @Mock
    MapDataApi mapDataApi;

    OsmWriteClient failsafeOsmFetchClient;

    @BeforeEach
    void setUp() {
        failsafeOsmFetchClient = new OsmWriteClient(mapDataApi);
    }

    @Test
    void shouldRetryOpenChangeset() {
        //  given
        Map<String, String> tags = Map.of("key", "value");
        when(mapDataApi.openChangeset(tags))
                .thenThrow(OsmConnectionException.class)
                .thenReturn(DEFAULT_ID);

        //  when
        long actualChangesetId = failsafeOsmFetchClient.openChangeset(tags);

        //  then
        verify(mapDataApi, times(2))
                .openChangeset(tags);
        assertThat(actualChangesetId).isEqualTo(DEFAULT_ID);
    }

    @Test
    void shouldUploadChangesToChangeset() {
        //  given
        doThrow(OsmConnectionException.class)
                .doNothing()
                .when(mapDataApi).uploadChanges(DEFAULT_ID, List.of(), null);

        //  when
        failsafeOsmFetchClient.uploadChanges(DEFAULT_ID, List.of());

        //  then
        verify(mapDataApi, times(2))
                .uploadChanges(DEFAULT_ID, List.of(), null);
    }

    @Test
    void shouldRetryClosingChangeset() {
        //  given
        doThrow(OsmConnectionException.class)
                .doNothing()
                .when(mapDataApi).closeChangeset(DEFAULT_ID);

        //  when
        failsafeOsmFetchClient.closeChangeset(DEFAULT_ID);

        //  then
        verify(mapDataApi, times(2))
                .closeChangeset(DEFAULT_ID);
    }
}
