package osm.bots.rings.inner.duplicates.fix;

import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.fix.generator.ReplaceRelationMemberFixGenerator;
import osm.bots.rings.inner.duplicates.osmapi.fetch.FetchClient;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;
import osm.bots.rings.inner.duplicates.osmapi.store.FixUploader;
import osm.bots.rings.inner.duplicates.osmose.DuplicatedViolation;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolations;
import osm.bots.rings.inner.duplicates.verifiers.DataVerifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class DuplicatedViolationsFixer extends ViolationsFixer {

    private final DataVerifier dataVerifier;

    DuplicatedViolationsFixer(FetchClient fetchClient, ReplaceRelationMemberFixGenerator fixGenerator, FixUploader fixUploader, DataVerifier dataVerifier) {
        super(fixGenerator, fixUploader, fetchClient);
        this.dataVerifier = dataVerifier;
    }

    @Override
    public void fix(OsmoseViolations osmoseViolations) {
        Partitions<DuplicatedViolation> partitions = osmoseViolations.getDuplicatedViolationsPartitions();
        partitions.getViolationsPartitions()
                .forEach(this::fixViolationsInSingleChangeset);
    }

    private void fixViolationsInSingleChangeset(List<DuplicatedViolation> violations) {
        List<List<ViolatingOsmData>> allViolatingData = fetchDuplicatedViolationsData(violations);
        List<List<ViolatingOsmData>> validDuplicatedViolations = filterDataQualifyingForFix(allViolatingData);
        List<ViolatingOsmData> violatingDataForUpload = validDuplicatedViolations.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        generateAndUploadFix(violatingDataForUpload);
    }

    private List<List<ViolatingOsmData>> filterDataQualifyingForFix(List<List<ViolatingOsmData>> allViolatingData) {
        return allViolatingData.stream()
                .filter(this::isWayWithTagsIdRepeating)
                .filter(this::hasNoRepeatingRelationIds)
                .filter(this::isValidatedByVerifiers)
                .collect(Collectors.toList());
    }

    private List<List<ViolatingOsmData>> fetchDuplicatedViolationsData(List<DuplicatedViolation> duplicatedViolationsPartition) {
        return duplicatedViolationsPartition.stream()
                .map(DuplicatedViolation::getViolations)
                .map(this::fetchViolationsData)
                .collect(Collectors.toList());
    }

    private boolean isWayWithTagsIdRepeating(List<ViolatingOsmData> duplicatedViolation) {
        long uniqueInnerRingIdsCount = getCountOfDistinctWayIds(duplicatedViolation, ViolatingOsmData::getInnerRingWay);
        long duplicatedWayIdsCount = getCountOfDistinctWayIds(duplicatedViolation, ViolatingOsmData::getDuplicatingWay);

        boolean isIdOfWayWithTagsRepeating = uniqueInnerRingIdsCount > duplicatedWayIdsCount
                && uniqueInnerRingIdsCount == duplicatedViolation.size();
        if (isIdOfWayWithTagsRepeating) {
            return true;
        }
        log.info("Did not detect repeating ids of way with tags in: {}", duplicatedViolation);
        return false;
    }

    private boolean hasNoRepeatingRelationIds(List<ViolatingOsmData> violationsData) {
        long countOfUniqueRelationIds = violationsData.stream()
                .mapToLong(violation -> violation.getRelation().getId())
                .distinct()
                .count();
        boolean relationIdsAreNotRepeating = violationsData.size() == countOfUniqueRelationIds;
        if (relationIdsAreNotRepeating) {
            return true;
        }
        log.info("Detected repeating relation ids in {}", violationsData);
        return false;
    }

    private long getCountOfDistinctWayIds(List<ViolatingOsmData> data, Function<ViolatingOsmData, WayWithParentRelations> wayFunction) {
        return data.stream()
                .map(wayFunction)
                .mapToLong(wayWithRelation -> wayWithRelation.getWay().getId())
                .distinct()
                .count();
    }

    private boolean isValidatedByVerifiers(List<ViolatingOsmData> duplicates) {
        boolean areAllDuplicatesValid = duplicates.stream()
                .allMatch(dataVerifier::qualifiesForBotFix);
        if (areAllDuplicatesValid) {
            return true;
        }
        log.info("At least one duplicated violation failed verifiers: {}", duplicates);
        return false;
    }
}
