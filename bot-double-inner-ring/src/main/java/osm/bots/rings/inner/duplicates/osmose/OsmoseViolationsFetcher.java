package osm.bots.rings.inner.duplicates.osmose;

import lombok.RequiredArgsConstructor;
import osm.bots.rings.inner.duplicates.utils.Partition;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class OsmoseViolationsFetcher {

    private final OsmoseViolationsReader reader;
    private final OsmoseViolationsValidator validator;
    private final DuplicatedViolationFilter duplicatedViolationsFilter;
    private final DuplicatedViolationPartitionCreator partitionCreator;
    private final int maxViolationsPerPartition;

    public OsmoseViolations fetchViolations() {
        List<InnerPolygonOsmoseViolation> allViolations = reader.read();
        List<InnerPolygonOsmoseViolation> validViolations = validator.getValidViolations(allViolations);

        Collection<List<InnerPolygonOsmoseViolation>> uniqueViolationChunks = getUniqueViolationsChunks(validViolations);
        Collection<List<DuplicatedViolation>> duplicatedViolationChunks = getDuplicatedViolationChunks(validViolations);

        return new OsmoseViolations(uniqueViolationChunks, duplicatedViolationChunks);
    }

    private Collection<List<InnerPolygonOsmoseViolation>> getUniqueViolationsChunks(List<InnerPolygonOsmoseViolation> validViolations) {
        List<InnerPolygonOsmoseViolation> uniqueViolations = duplicatedViolationsFilter.findUniqueViolations(validViolations);
        return Partition.partitionBySize(uniqueViolations, maxViolationsPerPartition);
    }

    private Collection<List<DuplicatedViolation>> getDuplicatedViolationChunks(List<InnerPolygonOsmoseViolation> validViolations) {
        List<DuplicatedViolation> duplicatedViolations = duplicatedViolationsFilter.findDuplicatedViolations(validViolations);
        return partitionCreator.createPartitions(duplicatedViolations, maxViolationsPerPartition);
    }
}
