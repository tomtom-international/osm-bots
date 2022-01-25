package osm.bots.rings.inner.duplicates.osmapi.store;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

@Slf4j
public class LogOnlyFixUploader implements FixUploader {

  @Override
  public void uploadFixesInSingleChangeset(List<ViolationFix> violationFixes) {
    log.info("Got {} fixes to upload in a single changeset", violationFixes.size());
    violationFixes.forEach(
        violationFix -> log.info("Violation fix: {}", violationFix)
    );
  }
}
