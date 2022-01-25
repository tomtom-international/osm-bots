package osm.bots.rings.inner.duplicates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.fetch.FetchClient;
import osm.bots.rings.inner.duplicates.osmapi.fix.ViolationFixGenerator;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;
import osm.bots.rings.inner.duplicates.osmapi.store.FixUploader;
import osm.bots.rings.inner.duplicates.osmose.DuplicatedInnerPolygonViolation;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolationsFetcher;
import osm.bots.rings.inner.duplicates.utils.Partition;
import osm.bots.rings.inner.duplicates.verifiers.DataVerifier;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
class ViolationsFixer {

    private final OsmoseViolationsFetcher osmoseViolationsFetcher;
    private final FetchClient fetchClient;
    private final DataVerifier dataVerifier;
    private final ViolationFixGenerator violationFixGenerator;
    private final FixUploader fixUploader;
    private final int maxViolationsPerChangeset;

    void fixViolations(Path path) {
        List<DuplicatedInnerPolygonViolation> osmoseViolations = osmoseViolationsFetcher.fetchViolations(path);
        Collection<List<DuplicatedInnerPolygonViolation>> violationChunks = Partition.partitionBySize(osmoseViolations, maxViolationsPerChangeset);
        violationChunks.forEach(this::fixInSingleChangeset);
    }

    private void fixInSingleChangeset(List<DuplicatedInnerPolygonViolation> osmoseViolations) {
        List<ViolatingOsmData> allViolatingData = fetchViolationsData(osmoseViolations);
        List<ViolatingOsmData> filteredViolationData = filterDataQualifyingForFix(allViolatingData);

        uploadViolations(filteredViolationData);
    }

    private List<ViolatingOsmData> fetchViolationsData(List<DuplicatedInnerPolygonViolation> osmoseViolations) {
        return osmoseViolations.stream()
                .map(fetchClient::fetchDataForViolation)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private List<ViolatingOsmData> filterDataQualifyingForFix(List<ViolatingOsmData> allViolatingData) {
        return allViolatingData.stream()
                .filter(dataVerifier::qualifiesForBotFix)
                .collect(Collectors.toList());
    }

    private void uploadViolations(List<ViolatingOsmData> filteredViolationData) {
        if (!filteredViolationData.isEmpty()) {
            List<ViolationFix> osmViolationFixes = violationFixGenerator.generateChanges(filteredViolationData);
            fixUploader.uploadFixesInSingleChangeset(osmViolationFixes);
        }
    }
}
