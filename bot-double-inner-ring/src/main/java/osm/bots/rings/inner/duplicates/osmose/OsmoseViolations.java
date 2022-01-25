package osm.bots.rings.inner.duplicates.osmose;

import lombok.Value;

import java.util.Collection;
import java.util.List;

@Value
public class OsmoseViolations {

    Collection<List<InnerPolygonOsmoseViolation>> uniqueViolationsPartitions;
    Collection<List<DuplicatedViolation>> duplicatedViolationsPartitions;
}
