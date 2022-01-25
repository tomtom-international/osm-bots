package osm.bots.rings.inner.duplicates.osmose;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class OsmoseViolationsFetcher {

    private final OsmoseViolationsReader reader;
    private final OsmoseViolationsValidator validator;
    private final DuplicatedViolationFilter duplicatedViolationsFilter;

    public List<InnerPolygonOsmoseViolation> fetchViolations(Path path) {
        List<InnerPolygonOsmoseViolation> allViolations = reader.read(path);
        List<InnerPolygonOsmoseViolation> deduplicatedViolations = duplicatedViolationsFilter.findUniqueViolations(allViolations);
        return validator.getValidViolations(deduplicatedViolations);
    }
}
