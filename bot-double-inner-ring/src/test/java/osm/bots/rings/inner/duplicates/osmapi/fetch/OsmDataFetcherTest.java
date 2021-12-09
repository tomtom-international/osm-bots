package osm.bots.rings.inner.duplicates.osmapi.fetch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import osm.bots.rings.inner.duplicates.RunParameters;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmose.DuplicatedInnerPolygonViolation;
import osm.bots.rings.inner.duplicates.osmose.ViolatingOsmIds;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {RunParameters.class, FetchConfiguration.class})
@EnableAutoConfiguration
class OsmDataFetcherTest {

    @Autowired
    private RunParameters runParameters;

    @Autowired
    private OsmDataFetcher osmDataFetcher;

    @Test
    void shouldFetchCorrectData() {
        // given
        long relationId = 2006740L;
        long innerRingWayId = 148813138L;
        long duplicatingWayId = 148813181L;
        ViolatingOsmIds violatingOsmIds = new ViolatingOsmIds(List.of(relationId), List.of(innerRingWayId, duplicatingWayId));

        // when
        Optional<ViolatingOsmData> violatingOsmData = osmDataFetcher.fetchDataForViolation(new DuplicatedInnerPolygonViolation(
                1170,
                violatingOsmIds));

        // then
        assertThat(violatingOsmData).isPresent();
        ViolatingOsmData violatingData = violatingOsmData.get();

        assertThat(violatingData.getRelation().getId()).isEqualTo(relationId);
        assertThat(violatingData.getDuplicatingWay().getWay().getId()).isEqualTo(duplicatingWayId);
        assertThat(violatingData.getInnerRingWay().getWay().getId()).isEqualTo(innerRingWayId);
    }
}
