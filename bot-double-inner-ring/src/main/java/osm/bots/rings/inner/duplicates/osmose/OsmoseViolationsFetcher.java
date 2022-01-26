package osm.bots.rings.inner.duplicates.osmose;

import lombok.RequiredArgsConstructor;
import osm.bots.rings.inner.duplicates.fix.Partitions;
import osm.bots.rings.inner.duplicates.statistics.StatisticsRepository;

import java.util.List;

@RequiredArgsConstructor
public class OsmoseViolationsFetcher {

    private final OsmoseViolationsReader reader;
    private final OsmoseViolationsValidator validator;
    private final DuplicatedViolationFilter duplicatedViolationsFilter;
    private final DuplicatedViolationPartitionCreator partitionCreator;
    private final StatisticsRepository statisticsRepository;
    private final int maxViolationsPerPartition;

    public OsmoseViolations fetchViolations() {
        List<InnerPolygonOsmoseViolation> allViolations = reader.read();
        List<InnerPolygonOsmoseViolation> validViolations = validator.getValidViolations(allViolations);
        statisticsRepository.setAllReadViolations(allViolations.size());

        Partitions<InnerPolygonOsmoseViolation> uniqueViolationsPartitions = getUniqueViolationsPartitions(validViolations);
        Partitions<DuplicatedViolation> duplicatedViolationsPartitions = getDuplicatedViolationsPartitions(validViolations);

        return new OsmoseViolations(uniqueViolationsPartitions, duplicatedViolationsPartitions);
    }

    private Partitions<InnerPolygonOsmoseViolation> getUniqueViolationsPartitions(List<InnerPolygonOsmoseViolation> validViolations) {
        List<InnerPolygonOsmoseViolation> uniqueViolations = duplicatedViolationsFilter.findUniqueViolations(validViolations);
        statisticsRepository.setUniqueReadViolations(uniqueViolations.size());
        return Partitions.partitionBySize(uniqueViolations, maxViolationsPerPartition);
    }

    private Partitions<DuplicatedViolation> getDuplicatedViolationsPartitions(List<InnerPolygonOsmoseViolation> validViolations) {
        List<DuplicatedViolation> duplicatedViolations = duplicatedViolationsFilter.findDuplicatedViolations(validViolations);
        long allDuplicatedViolationsCount = duplicatedViolations.stream()
                .mapToLong(s -> s.getViolations().size())
                .sum();
        statisticsRepository.setDuplicatedReadViolations(allDuplicatedViolationsCount);
        return partitionCreator.createPartitions(duplicatedViolations, maxViolationsPerPartition);
    }
}
