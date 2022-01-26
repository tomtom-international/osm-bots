package osm.bots.rings.inner.duplicates.osmapi.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;
import osm.bots.rings.inner.duplicates.statistics.StatisticsRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class LogOnlyFixUploader implements FixUploader {

    private final StatisticsRepository statisticsRepository;

    @Override
    public void uploadFixesInSingleChangeset(List<ViolationFix> violationFixes) {
        log.info("Got {} fixes to upload in a single changeset", violationFixes.size());
        violationFixes.forEach(
                violationFix -> log.info("Violation fix: {}", violationFix)
        );
        statisticsRepository.addUploadedViolations(violationFixes.size());
    }
}
