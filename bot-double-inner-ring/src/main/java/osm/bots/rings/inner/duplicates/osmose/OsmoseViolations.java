package osm.bots.rings.inner.duplicates.osmose;

import lombok.Value;
import osm.bots.rings.inner.duplicates.fix.Partitions;

@Value
public class OsmoseViolations {

    Partitions<InnerPolygonOsmoseViolation> uniqueViolationsPartitions;
    Partitions<DuplicatedViolation> duplicatedViolationsPartitions;
}
