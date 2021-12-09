package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.RelationMember;
import de.westnordost.osmapi.map.data.Way;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;
import osm.bots.rings.inner.duplicates.osmose.DuplicatedInnerPolygonViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class OsmDataFetcher {

    private final OsmFetchClient osmFetchClient;

    private int violationCounter = 0;

    public Optional<ViolatingOsmData> fetchDataForViolation(DuplicatedInnerPolygonViolation violation) {
        List<Way> osmWays = fetchWays(violation);
        Relation osmRelation = fetchRelation(violation);

        if (isViolationDataConsistent(osmWays, osmRelation)) {
            logFetchProgress();
            return createViolatingOsmData(osmWays, osmRelation);
        } else {
            log.warn("Incomplete data for relation id {} and way ids {}", violation.getViolatingRelationId(), violation.getPairOfViolatingWaysIds());
            return Optional.empty();
        }
    }

    private List<Way> fetchWays(DuplicatedInnerPolygonViolation violation) {
        /*
          getWay() method is used instead of getWays(), because getWay() returns null in case of deleted OSM elements
          we want to filter out cases where any of elements were deleted. Unfortunately getWays() returns elements even
          if they were deleted (without setting any kind of deleted flag), so it's impossible to know if it still exists or not.
         */
        return violation.getPairOfViolatingWaysIds()
                .stream()
                .map(osmFetchClient::getWay)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Relation fetchRelation(DuplicatedInnerPolygonViolation violation) {
        return osmFetchClient
                .getRelation(violation.getViolatingRelationId());
    }

    private boolean isViolationDataConsistent(List<Way> osmWays, Relation osmRelation) {
        return osmWays.size() == 2 && osmRelation != null;
    }

    private void logFetchProgress() {
        if (++violationCounter % 500 == 0) {
            log.info("Fetched {} violations", violationCounter);
        }
    }

    private Optional<ViolatingOsmData> createViolatingOsmData(List<Way> osmWays, Relation osmRelation) {
        Optional<Way> innerRingWayCandidate = getInnerRingWay(osmWays, osmRelation);

        if (innerRingWayCandidate.isPresent()) {
            Way innerRingWay = innerRingWayCandidate.get();
            Way duplicatingWay = getDuplicatingWay(osmWays, innerRingWay);
            return Optional.of(new ViolatingOsmData(
                    osmRelation,
                    new WayWithParentRelations(innerRingWay, osmFetchClient.getRelationsForWay(innerRingWay.getId())),
                    new WayWithParentRelations(duplicatingWay, osmFetchClient.getRelationsForWay(duplicatingWay.getId()))));
        } else {
            log.warn("Incomplete data for violation relation id {}", osmRelation.getId());
            return Optional.empty();
        }
    }

    private Optional<Way> getInnerRingWay(List<Way> osmWays, Relation osmRelation) {
        List<Long> relationMemberIds = osmRelation.getMembers()
                .stream()
                .map(RelationMember::getRef)
                .collect(Collectors.toList());

        return osmWays.stream()
                .filter(way -> relationMemberIds.contains(way.getId()))
                .findAny();
    }

    private Way getDuplicatingWay(List<Way> osmWays, Way innerRingWay) {
        List<Way> osmWaysNotMatchingRelation = new ArrayList<>(osmWays);
        osmWaysNotMatchingRelation.remove(innerRingWay);
        return osmWaysNotMatchingRelation.get(0);
    }
}

