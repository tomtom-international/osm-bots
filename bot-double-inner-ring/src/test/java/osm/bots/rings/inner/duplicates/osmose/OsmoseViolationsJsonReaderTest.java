package osm.bots.rings.inner.duplicates.osmose;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OsmoseViolationsJsonReaderTest {

    @Test
    void shouldReadViolationFromJson() {
        //  given
        Path jsonPath = Paths.get("src", "test", "resources", "exampleOsmoseViolation.json");
        OsmoseViolationsJsonReader osmoseViolationJsonReader = new OsmoseViolationsJsonReader(jsonPath);
        InnerPolygonOsmoseViolation expectedViolation = new InnerPolygonOsmoseViolation(
                1170,
                new ViolatingOsmIds(List.of(111L), List.of(222L, 333L)));

        //  when
        List<InnerPolygonOsmoseViolation> actualViolations = osmoseViolationJsonReader.read();

        assertThat(actualViolations)
                .containsExactlyInAnyOrder(expectedViolation);
    }
}
