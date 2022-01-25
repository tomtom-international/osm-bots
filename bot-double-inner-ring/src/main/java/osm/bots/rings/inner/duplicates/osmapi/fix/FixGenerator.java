package osm.bots.rings.inner.duplicates.osmapi.fix;

import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

import java.util.List;

public interface FixGenerator {

    List<ViolationFix> generateFixes(List<ViolatingOsmData> violatingOsmData);
}
