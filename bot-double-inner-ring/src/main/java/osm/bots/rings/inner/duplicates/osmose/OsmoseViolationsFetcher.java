package osm.bots.rings.inner.duplicates.osmose;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class OsmoseViolationsFetcher {

    private final OsmoseViolationsJsonReader reader;
    private final OsmoseViolationsValidator validator;
    private final DuplicatedViolationFilter duplicatedViolationsFilter;

    public List<DuplicatedInnerPolygonViolation> fetchViolations(Path path) {
        List<DuplicatedInnerPolygonViolation> allViolations = reader.read(path);
        List<DuplicatedInnerPolygonViolation> deduplicatedViolations = duplicatedViolationsFilter.deduplicate(allViolations);
        return validator.getValidViolations(deduplicatedViolations);
    }
}
