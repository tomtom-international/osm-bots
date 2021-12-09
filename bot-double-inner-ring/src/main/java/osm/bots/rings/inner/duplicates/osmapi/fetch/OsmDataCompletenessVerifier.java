package osm.bots.rings.inner.duplicates.osmapi.fetch;

import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
class OsmDataCompletenessVerifier {

    boolean isDataIncomplete(OsmData osmData) {
        return getCountOfNonNullWays(osmData) != 2
                || osmData.getRelation() == null;
    }

    private long getCountOfNonNullWays(OsmData osmData) {
        return osmData
                .getWaysWithParentRelations()
                .stream()
                .filter(Objects::nonNull)
                .count();
    }
}
