package osm.bots.rings.inner.duplicates.statistics;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Getter
public class StatisticsRepository {

    private static final int MILLISECONDS_TO_MINUTES_FACTOR = 60000;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final AtomicLong applicationStartTime = new AtomicLong(0);
    private final AtomicLong applicationStopTime = new AtomicLong(0);
    private final AtomicLong allReadViolations = new AtomicLong(0);
    private final AtomicLong uniqueReadViolations = new AtomicLong(0);
    private final AtomicLong duplicatedReadViolations = new AtomicLong(0);
    private final AtomicLong uniqueViolationsPassedFilters = new AtomicLong(0);
    private final AtomicLong duplicatedViolationsPassedFilters = new AtomicLong(0);
    private final AtomicLong uploadedViolations = new AtomicLong(0);
    private final AtomicLong osmUploadedConflicts = new AtomicLong(0);
    private final AtomicLong openedChangesets = new AtomicLong(0);
    private final AtomicLong rejectedByVerifiers = new AtomicLong(0);

    public void setApplicationStartTime(long count) {
        this.applicationStartTime.set(count);
    }

    public void setApplicationStopTime(long count) {
        this.applicationStopTime.set(count);
    }

    public void setAllReadViolations(long count) {
        this.allReadViolations.set(count);
    }

    public void setUniqueReadViolations(long count) {
        this.uniqueReadViolations.set(count);
    }

    public void setDuplicatedReadViolations(long count) {
        this.duplicatedReadViolations.set(count);
    }

    public void addUniqueViolationPassedFilters(long count) {
        this.uniqueViolationsPassedFilters.addAndGet(count);
    }

    public void addDuplicatedViolationPassedFilters(long count) {
        this.duplicatedViolationsPassedFilters.addAndGet(count);
    }

    public void addUploadedViolations(long count) {
        this.uploadedViolations.addAndGet(count);
    }

    public void addOsmUploadConflicts(long count) {
        this.osmUploadedConflicts.addAndGet(count);
    }

    public void addOpenedChangesets(long count) {
        this.openedChangesets.addAndGet(count);
    }

    public void addRejectedByVerifiers(long count) {
        this.rejectedByVerifiers.addAndGet(count);
    }

    public void printRunSummary() {
        log.info("====================================================");
        log.info("Inner Ring Bot summary:");
        log.info("Processing started at: {}", convertTime(applicationStartTime.get()));
        log.info("Processing finished at: {}", convertTime(applicationStopTime.get()));
        log.info("Process duration: {} [min]", calculateProcessDuration());
        log.info("Count of all violations read from file: {}", allReadViolations.get());
        log.info("Count of unique violations read from file: {}", uniqueReadViolations.get());
        log.info("Count of unique violations passed throughout filters: {}", uniqueViolationsPassedFilters.get());
        log.info("Count of duplicated violations passed throughout filters: {}", duplicatedViolationsPassedFilters.get());
        log.info("Count of duplicated violations read from file: {}", duplicatedReadViolations.get());
        log.info("Count of violations rejected by verifiers: {}", rejectedByVerifiers.get());
        log.info("Count of opened changesets: {}", openedChangesets.get());
        log.info("Count of violation fixes uploaded to OSM: {}", uploadedViolations.get());
        log.info("Count of conflicted violation fixes: {}", osmUploadedConflicts.get());
        log.info("Count of not fixed violations: {}", allReadViolations.get() - uploadedViolations.get());
        log.info("====================================================");
    }

    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(DATE_TIME_FORMAT);
        return format.format(date);
    }

    private long calculateProcessDuration() {
        return (applicationStopTime.get() - applicationStartTime.get()) / MILLISECONDS_TO_MINUTES_FACTOR;
    }
}
