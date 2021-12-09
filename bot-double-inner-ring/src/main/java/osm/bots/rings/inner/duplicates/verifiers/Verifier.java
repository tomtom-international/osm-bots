package osm.bots.rings.inner.duplicates.verifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.function.Predicate;

@Slf4j
@Getter
@RequiredArgsConstructor
abstract class Verifier implements Predicate<ViolatingOsmData> {

    @Override
    public boolean test(ViolatingOsmData violatingOsmData) {
        if (!isMatchingVerifierCriteria(violatingOsmData)) {
            log.warn("Verifier: {} failed for violation {}", this.getClass().getSimpleName(), violatingOsmData);
            return false;
        }
        return true;
    }

    abstract boolean isMatchingVerifierCriteria(ViolatingOsmData violatingOsmData);
}
