package osm.bots.rings.inner.duplicates.osmose;

import lombok.Value;

import java.util.List;

@Value
public class DuplicatedViolation {

    List<InnerPolygonOsmoseViolation> violations;
}
