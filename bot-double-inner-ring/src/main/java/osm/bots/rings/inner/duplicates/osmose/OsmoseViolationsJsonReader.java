package osm.bots.rings.inner.duplicates.osmose;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
class OsmoseViolationsJsonReader implements OsmoseViolationsReader {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final TypeReference<List<InnerPolygonOsmoseViolation>> VIOLATION_TYPE_REFERENCE = new TypeReference<>() {
    };
    private final Path jsonPath;

    @Override
    public List<InnerPolygonOsmoseViolation> read() {
        try {
            List<InnerPolygonOsmoseViolation> innerPolygonOsmoseViolations = JSON_MAPPER.readValue(jsonPath.toFile(), VIOLATION_TYPE_REFERENCE);
            log.info("{} violations read from: {}", innerPolygonOsmoseViolations.size(), jsonPath);
            return innerPolygonOsmoseViolations;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read json file from: " + jsonPath, e);
        }
    }
}
