package osm.bots.rings.inner.duplicates.osmose;

import java.nio.file.Path;
import java.util.List;

public interface OsmoseViolationsReader {

    List<InnerPolygonOsmoseViolation> read(Path path);
}
