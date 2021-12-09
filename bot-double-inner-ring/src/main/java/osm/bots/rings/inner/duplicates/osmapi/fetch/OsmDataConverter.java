package osm.bots.rings.inner.duplicates.osmapi.fetch;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;

import java.util.Optional;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

@Slf4j
@UtilityClass
class OsmDataConverter {

    Optional<ViolatingOsmData> convert(OsmData osmData) {
        Optional<WayWithParentRelations> innerRingWayCandidate = getFeatureCandidate(hasParentRelations(), osmData);
        Optional<WayWithParentRelations> duplicatingWayCandidate = getFeatureCandidate(not(hasParentRelations()), osmData);

        if (duplicatingWayCandidate.isPresent() && innerRingWayCandidate.isPresent()) {
            return Optional.of(new ViolatingOsmData(
                    osmData.getRelation(),
                    innerRingWayCandidate.get(),
                    duplicatingWayCandidate.get()
            ));
        }
        log.warn("Incomplete data for violation relation id {}", osmData.getRelation().getId());
        return Optional.empty();
    }

    private Optional<WayWithParentRelations> getFeatureCandidate(Predicate<WayWithParentRelations> predicate, OsmData osmData) {
        return osmData
                .getWaysWithParentRelations()
                .stream()
                .filter(predicate)
                .findFirst();
    }

    private Predicate<WayWithParentRelations> hasParentRelations() {
        return feature -> !feature.getParentRelations().isEmpty();
    }
}
