package osm.bots.rings.inner.duplicates.fix.generator;

import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

import java.util.List;
import java.util.stream.Collectors;

public interface FixGenerator {

    default List<ViolationFix> generateFixes(List<ViolatingOsmData> violatingOsmData) {
        return violatingOsmData.stream()
                .map(this::generateFix)
                .collect(Collectors.toList());
    }

    ViolationFix generateFix(ViolatingOsmData violatingOsmData);
}
