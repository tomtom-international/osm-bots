package osm.bots.rings.inner.duplicates.osmose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OsmoseViolationsJsonReaderTest {

    private OsmoseViolationsJsonReader osmoseViolationJsonReader;

    @BeforeEach
    void setUp() {
        this.osmoseViolationJsonReader = new OsmoseViolationsJsonReader();
    }

    @Test
    void shouldReadViolationFromJson() {
        //  given
        Path jsonPath = Paths.get("src", "test", "resources", "exampleOsmoseViolation.json");
        InnerPolygonOsmoseViolation expectedViolation = new InnerPolygonOsmoseViolation(
                1170,
                new ViolatingOsmIds(List.of(111L), List.of(222L, 333L)));

        //  when
        List<InnerPolygonOsmoseViolation> actualViolations = osmoseViolationJsonReader.read(jsonPath);

        assertThat(actualViolations)
                .containsExactlyInAnyOrder(expectedViolation);
    }
}
