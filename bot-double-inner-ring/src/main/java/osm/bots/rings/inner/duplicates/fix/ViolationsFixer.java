package osm.bots.rings.inner.duplicates.fix;

import lombok.AllArgsConstructor;
import osm.bots.rings.inner.duplicates.fix.generator.FixGenerator;
import osm.bots.rings.inner.duplicates.osmapi.fetch.FetchClient;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;
import osm.bots.rings.inner.duplicates.osmapi.store.FixUploader;
import osm.bots.rings.inner.duplicates.osmose.InnerPolygonOsmoseViolation;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public abstract class ViolationsFixer {

    private final FixGenerator fixGenerator;
    private final FixUploader fixUploader;
    private final FetchClient fetchClient;

    public abstract void fix(OsmoseViolations osmoseViolations);

    protected List<ViolatingOsmData> fetchViolationsData(List<InnerPolygonOsmoseViolation> osmoseViolations) {
        return osmoseViolations.stream()
                .map(fetchClient::fetchDataForViolation)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    protected void generateAndUploadFix(List<ViolatingOsmData> filteredViolationData) {
        if (!filteredViolationData.isEmpty()) {
            List<ViolationFix> osmViolationFixes = fixGenerator.generateFixes(filteredViolationData);
            fixUploader.uploadFixesInSingleChangeset(osmViolationFixes);
        }
    }
}
