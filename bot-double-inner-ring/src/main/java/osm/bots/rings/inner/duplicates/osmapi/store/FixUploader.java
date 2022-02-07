package osm.bots.rings.inner.duplicates.osmapi.store;

import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

import java.util.List;

public interface FixUploader {

  void uploadFixesInSingleChangeset(List<ViolationFix> violationFixes);
}
