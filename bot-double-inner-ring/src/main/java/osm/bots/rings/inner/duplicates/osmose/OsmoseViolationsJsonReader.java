package osm.bots.rings.inner.duplicates.osmose;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
class OsmoseViolationsJsonReader {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final TypeReference<List<InnerPolygonOsmoseViolation>> VIOLATION_TYPE_REFERENCE = new TypeReference<>() {
    };

    List<InnerPolygonOsmoseViolation> read(Path path) {
        try {
            List<InnerPolygonOsmoseViolation> innerPolygonOsmoseViolations = JSON_MAPPER.readValue(path.toFile(), VIOLATION_TYPE_REFERENCE);
            log.info("{} violations read from: {}", innerPolygonOsmoseViolations.size(), path);
            return innerPolygonOsmoseViolations;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read json file from: " + path, e);
        }
    }
}
