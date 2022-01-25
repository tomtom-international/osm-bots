package osm.bots.rings.inner.duplicates.osmapi.store;

import java.util.List;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

public interface FixUploader {

  void uploadFixesInSingleChangeset(List<ViolationFix> violationFixes);
}
