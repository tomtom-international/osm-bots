package osm.bots.rings.inner.duplicates.verifiers;

import lombok.RequiredArgsConstructor;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.statistics.StatisticsRepository;

import java.util.List;

@RequiredArgsConstructor
public class DataVerifier {

    private final List<Verifier> verifiers;
    private final StatisticsRepository statisticsRepository;

    public boolean qualifiesForBotFix(ViolatingOsmData violatingData) {
        boolean violationPassVerifiers = verifiers.stream()
                .allMatch(verifier -> verifier.test(violatingData));
        if (not(violationPassVerifiers)) {
            statisticsRepository.addRejectedByVerifiers(1);
        }
        return violationPassVerifiers;
    }

    private boolean not(boolean verifierResult) {
        return !verifierResult;
    }
}
