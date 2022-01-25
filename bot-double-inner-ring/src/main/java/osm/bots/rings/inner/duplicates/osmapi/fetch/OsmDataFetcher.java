package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;
import osm.bots.rings.inner.duplicates.osmose.InnerPolygonOsmoseViolation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
class OsmDataFetcher {

    private final OsmDataClient osmDataClient;

    private int violationCounter = 0;

    OsmData fetch(InnerPolygonOsmoseViolation violation) {
        Relation osmRelation = fetchRelation(violation);
        List<Way> osmWays = fetchWays(violation);
        List<WayWithParentRelations> wayWithParentRelations = fetchParentRelations(osmWays);
        logFetchProgress();

        return new OsmData(osmRelation, wayWithParentRelations);
    }

    private List<WayWithParentRelations> fetchParentRelations(List<Way> osmWays) {
        return osmWays.stream()
                .map(way -> new WayWithParentRelations(way, osmDataClient.getRelationsForWay(way.getId())))
                .collect(Collectors.toList());
    }

    private List<Way> fetchWays(InnerPolygonOsmoseViolation violation) {
        /*
          getWay() method is used instead of getWays(), because getWay() returns null in case of deleted OSM elements
          we want to filter out cases where any of elements were deleted. Unfortunately getWays() returns elements even
          if they were deleted (without setting any kind of deleted flag), so it's impossible to know if it still exists or not.
         */
        return violation.getPairOfViolatingWaysIds()
                .stream()
                .map(osmDataClient::getWay)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Relation fetchRelation(InnerPolygonOsmoseViolation violation) {
        return osmDataClient
                .getRelation(violation.getViolatingRelationId());
    }

    private void logFetchProgress() {
        if (++violationCounter % 500 == 0) {
            log.info("Fetched {} violations", violationCounter);
        }
    }
}

