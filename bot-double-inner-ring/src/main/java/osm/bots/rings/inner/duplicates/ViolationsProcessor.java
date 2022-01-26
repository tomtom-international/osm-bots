package osm.bots.rings.inner.duplicates;

import lombok.RequiredArgsConstructor;
import osm.bots.rings.inner.duplicates.fix.ViolationsFixer;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolations;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolationsFetcher;

import java.util.List;

@RequiredArgsConstructor
class ViolationsProcessor {

    final List<ViolationsFixer> violationsFixers;
    final OsmoseViolationsFetcher osmoseViolationsFetcher;

    void processViolations() {
        OsmoseViolations osmoseViolations = osmoseViolationsFetcher.fetchViolations();
        violationsFixers.forEach(violationsFixer -> violationsFixer.fix(osmoseViolations));
    }
}
