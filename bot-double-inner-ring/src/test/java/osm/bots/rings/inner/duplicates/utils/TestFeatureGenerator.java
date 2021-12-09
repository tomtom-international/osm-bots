package osm.bots.rings.inner.duplicates.utils;

import de.westnordost.osmapi.map.data.OsmRelation;
import de.westnordost.osmapi.map.data.OsmWay;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import lombok.experimental.UtilityClass;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@UtilityClass
public class TestFeatureGenerator {

    public static final AtomicLong NEXT_AUTOGENERATED_WAY_ID = new AtomicLong(1);
    private static final long DEFAULT_RELATION_ID = 1L;

    public static WayWithParentRelations createWayViolation(
            List<Long> wayNodes,
            Map<String, String> wayTags,
            List<Long> parentRelationIds) {
        return createWayViolation(NEXT_AUTOGENERATED_WAY_ID.getAndIncrement(), wayNodes, wayTags, parentRelationIds);
    }

    public static WayWithParentRelations createWayViolation(
            long wayId,
            List<Long> wayNodes,
            Map<String, String> wayTags,
            List<Long> parentRelationIds) {
        List<Relation> parentRelations = createRelations(parentRelationIds);
        Way wayInViolatingRelation = new OsmWay(wayId, 1, wayNodes, wayTags);
        return new WayWithParentRelations(wayInViolatingRelation, parentRelations);
    }

    public static OsmRelation createRelation() {
        return createRelation(DEFAULT_RELATION_ID, null);
    }

    public static OsmRelation createRelation(Long violatingRelationId) {
        return createRelation(violatingRelationId, null);
    }

    public static OsmRelation createRelation(Map<String, String> tags) {
        return createRelation(DEFAULT_RELATION_ID, tags);
    }

    public static OsmRelation createRelation(Long violatingRelationId, Map<String, String> tags) {
        Long relationId = getOrDefault(violatingRelationId, DEFAULT_RELATION_ID);
        return new OsmRelation(relationId, 1, List.of(), tags);
    }

    public static List<Relation> createRelations(List<Long> parentRelationIds) {
        return parentRelationIds.stream()
                .map(TestFeatureGenerator::createRelation)
                .collect(Collectors.toList());
    }

    public static <T> T getOrDefault(T value, T defaultValue) {
        return value == null
                ? defaultValue
                : value;
    }
}