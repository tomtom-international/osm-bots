package osm.bots.rings.inner.duplicates.verifiers;

import lombok.RequiredArgsConstructor;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.Collection;

@RequiredArgsConstructor
public class DataVerifier {

    private final Collection<Verifier> verifiers;

    public boolean qualifiesForBotFix(ViolatingOsmData violatingData) {
        return verifiers.stream()
                .allMatch(verifier -> verifier.test(violatingData));
    }
}
