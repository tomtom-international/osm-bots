package osm.bots.rings.inner.duplicates.osmose;

import java.util.List;

public interface OsmoseViolationsReader {

    List<InnerPolygonOsmoseViolation> read();
}
