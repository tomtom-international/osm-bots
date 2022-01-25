package osm.bots.rings.inner.duplicates.verifiers;

import lombok.RequiredArgsConstructor;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;

import java.util.List;

@RequiredArgsConstructor
public class DataVerifier {

    private final List<Verifier> verifiers;

    public boolean qualifiesForBotFix(ViolatingOsmData violatingData) {
        return verifiers.stream()
                .allMatch(verifier -> verifier.test(violatingData));
    }
}
