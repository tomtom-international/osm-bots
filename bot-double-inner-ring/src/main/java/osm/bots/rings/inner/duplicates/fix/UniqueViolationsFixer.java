package osm.bots.rings.inner.duplicates.fix;

import osm.bots.rings.inner.duplicates.fix.generator.ReplaceWayTagsFixGenerator;
import osm.bots.rings.inner.duplicates.osmapi.fetch.FetchClient;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.store.FixUploader;
import osm.bots.rings.inner.duplicates.osmose.InnerPolygonOsmoseViolation;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolations;
import osm.bots.rings.inner.duplicates.verifiers.DataVerifier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UniqueViolationsFixer extends ViolationsFixer {

    private final DataVerifier dataVerifier;

    public UniqueViolationsFixer(FetchClient fetchClient, ReplaceWayTagsFixGenerator fixGenerator, FixUploader fixUploader, DataVerifier verifier) {
        super(fixGenerator, fixUploader, fetchClient);
        dataVerifier = verifier;
    }

    @Override
    public void fix(OsmoseViolations osmoseViolations) {
        Collection<List<InnerPolygonOsmoseViolation>> partitions = osmoseViolations.getUniqueViolationsPartitions();
        partitions.forEach(this::fixViolationsInSingleChangeset);
    }

    private void fixViolationsInSingleChangeset(List<InnerPolygonOsmoseViolation> violations) {
        List<ViolatingOsmData> allViolatingData = fetchViolationsData(violations);
        List<ViolatingOsmData> filteredViolationData = filterDataQualifyingForFix(allViolatingData);
        generateAndUploadFix(filteredViolationData);
    }

    private List<ViolatingOsmData> filterDataQualifyingForFix(List<ViolatingOsmData> allViolatingData) {
        return allViolatingData.stream()
                .filter(dataVerifier::qualifiesForBotFix)
                .collect(Collectors.toList());
    }
}