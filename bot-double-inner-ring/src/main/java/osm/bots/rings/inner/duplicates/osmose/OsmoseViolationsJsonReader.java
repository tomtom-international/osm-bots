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
    private static final TypeReference<List<DuplicatedInnerPolygonViolation>> VIOLATION_TYPE_REFERENCE = new TypeReference<>() {
    };

    List<DuplicatedInnerPolygonViolation> read(Path path) {
        try {
            List<DuplicatedInnerPolygonViolation> duplicatedInnerPolygonViolations = JSON_MAPPER.readValue(path.toFile(), VIOLATION_TYPE_REFERENCE);
            log.info("{} violations read from: {}", duplicatedInnerPolygonViolations.size(), path);
            return duplicatedInnerPolygonViolations;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read json file from: " + path, e);
        }
    }
}
