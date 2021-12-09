package osm.bots.rings.inner.duplicates.osmapi.fetch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmose.DuplicatedInnerPolygonViolation;
import osm.bots.rings.inner.duplicates.osmose.ViolatingOsmIds;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FetchClientTest {

    @Mock
    private OsmDataFetcher osmDataFetcher;

    private FetchClient fetchClient;

    @BeforeEach
    void setUp() {
        fetchClient = new FetchClient(osmDataFetcher);
    }

    @Test
    void shouldFetchDataForGivenViolation() {
        // given
        final long WAY_1_ID = 3L;
        final long WAY_2_ID = 4L;
        final long RELATION_ID = 2L;
        DuplicatedInnerPolygonViolation violation =
                new DuplicatedInnerPolygonViolation(1170, new ViolatingOsmIds(List.of(RELATION_ID), List.of(WAY_1_ID, WAY_2_ID)));
        when(osmDataFetcher.fetch(violation))
                .thenReturn(TestFetchDataGenerator.createOsmData(RELATION_ID, WAY_1_ID, WAY_2_ID));
        // when
        Optional<ViolatingOsmData> actual = fetchClient.fetchDataForViolation(violation);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .isEqualTo(TestFetchDataGenerator.createViolatingOsmData(RELATION_ID, WAY_1_ID, WAY_2_ID));
    }

    @Test
    void shouldReturnNoDataWhenFetchedViolationIsNotComplete() {
        // given
        final long WAY_1_ID = 2L;
        final long RELATION_ID = 1L;
        DuplicatedInnerPolygonViolation violation = mock(DuplicatedInnerPolygonViolation.class);

        OsmData osmData = TestFetchDataGenerator.osmData()
                .relationId(RELATION_ID)
                .wayWithParentRelations(TestFeatureGenerator.wayWithParentRelations()
                        .wayId(WAY_1_ID)
                        .parentRelationId(RELATION_ID)
                        .build())
                .build();
        when(osmDataFetcher.fetch(violation)).thenReturn(osmData);

        // when
        Optional<ViolatingOsmData> actual = fetchClient.fetchDataForViolation(violation);

        // then
        assertThat(actual).isEmpty();
    }
}
