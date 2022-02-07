package osm.bots.rings.inner.duplicates.fix;

import osm.bots.rings.inner.duplicates.fix.generator.ReplaceWayTagsFixGenerator;
import osm.bots.rings.inner.duplicates.osmapi.fetch.FetchClient;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.store.FixUploader;
import osm.bots.rings.inner.duplicates.osmose.InnerPolygonOsmoseViolation;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolations;
import osm.bots.rings.inner.duplicates.statistics.StatisticsRepository;
import osm.bots.rings.inner.duplicates.verifiers.DataVerifier;

import java.util.List;
import java.util.stream.Collectors;

public class UniqueViolationsFixer extends ViolationsFixer {

    private final DataVerifier dataVerifier;
    private final StatisticsRepository statisticsRepository;

    public UniqueViolationsFixer(
            FetchClient fetchClient,
            ReplaceWayTagsFixGenerator fixGenerator,
            FixUploader fixUploader,
            DataVerifier verifier,
            StatisticsRepository statisticsRepository) {
        super(fixGenerator, fixUploader, fetchClient);
        dataVerifier = verifier;
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public void fix(OsmoseViolations osmoseViolations) {
        Partitions<InnerPolygonOsmoseViolation> partitions = osmoseViolations.getUniqueViolationsPartitions();
        partitions.getViolationsPartitions()
                .forEach(this::fixViolationsInSingleChangeset);
    }

    private void fixViolationsInSingleChangeset(List<InnerPolygonOsmoseViolation> violations) {
        List<ViolatingOsmData> allViolatingData = fetchViolationsData(violations);
        List<ViolatingOsmData> filteredViolationData = filterDataQualifyingForFix(allViolatingData);
        statisticsRepository.addUniqueViolationPassedFilters(filteredViolationData.size());
        generateAndUploadFix(filteredViolationData);
    }

    private List<ViolatingOsmData> filterDataQualifyingForFix(List<ViolatingOsmData> allViolatingData) {
        return allViolatingData.stream()
                .filter(dataVerifier::qualifiesForBotFix)
                .collect(Collectors.toList());
    }
}
