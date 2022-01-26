package osm.bots.rings.inner.duplicates.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StatisticsRepositoryTest {

    private StatisticsRepository statisticsRepository;

    @BeforeEach
    void setUp() {
        this.statisticsRepository = new StatisticsRepository();
    }

    @Test
    void shouldInitializeProperly() {
        assertThat(statisticsRepository)
                .returns(0L, results -> results.getApplicationStartTime().get())
                .returns(0L, results -> results.getApplicationStopTime().get())
                .returns(0L, results -> results.getAllReadViolations().get())
                .returns(0L, results -> results.getUniqueReadViolations().get())
                .returns(0L, results -> results.getDuplicatedReadViolations().get())
                .returns(0L, results -> results.getRejectedByVerifiers().get())
                .returns(0L, results -> results.getOpenedChangesets().get())
                .returns(0L, results -> results.getUploadedViolations().get())
                .returns(0L, results -> results.getOsmUploadedConflicts().get());
    }

    @Test
    void shouldSetApplicationStartTime() {
        //  given
        long applicationStartTime = System.currentTimeMillis();

        //  when
        statisticsRepository.setApplicationStartTime(applicationStartTime);

        //  then
        assertThat(statisticsRepository.getApplicationStartTime().get())
                .isEqualTo(applicationStartTime);
    }

    @Test
    void shouldAddApplicationStopTime() {
        //  given
        long applicationStopTime = System.currentTimeMillis();

        //  when
        statisticsRepository.setApplicationStopTime(applicationStopTime);

        //  then
        assertThat(statisticsRepository.getApplicationStopTime().get())
                .isEqualTo(applicationStopTime);
    }

    @Test
    void shouldAddAllReadViolations() {
        //  when
        statisticsRepository.setUniqueReadViolations(2);
        statisticsRepository.setAllReadViolations(1);

        //  then
        assertThat(statisticsRepository.getAllReadViolations().get())
                .isEqualTo(1);
    }

    @Test
    void shouldAddUniqueReadViolations() {
        //  when
        statisticsRepository.setUniqueReadViolations(2);
        statisticsRepository.setUniqueReadViolations(1);

        //  then
        assertThat(statisticsRepository.getUniqueReadViolations().get())
                .isEqualTo(1);
    }

    @Test
    void shouldAddDuplicatedReadViolations() {
        //  when
        statisticsRepository.setDuplicatedReadViolations(1);
        statisticsRepository.setDuplicatedReadViolations(2);

        //  then
        assertThat(statisticsRepository.getDuplicatedReadViolations().get())
                .isEqualTo(2);
    }

    @Test
    void shouldAddRejectedByVerifiers() {
        //  when
        statisticsRepository.addRejectedByVerifiers(1);
        statisticsRepository.addRejectedByVerifiers(2);
        statisticsRepository.addRejectedByVerifiers(0);

        //  then
        assertThat(statisticsRepository.getRejectedByVerifiers().get())
                .isEqualTo(3);
    }

    @Test
    void shouldAddOpenedChangesets() {
        //  given
        long given = 1;

        //  when
        statisticsRepository.addOpenedChangesets(2);
        statisticsRepository.addOpenedChangesets(0);
        statisticsRepository.addOpenedChangesets(1);


        //  then
        assertThat(statisticsRepository.getOpenedChangesets().get())
                .isEqualTo(3);
    }

    @Test
    void shouldAddUploadedViolations() {
        //  when
        statisticsRepository.addUploadedViolations(1);
        statisticsRepository.addUploadedViolations(0);
        statisticsRepository.addUploadedViolations(2);

        //  then
        assertThat(statisticsRepository.getUploadedViolations().get())
                .isEqualTo(3);
    }

    @Test
    void shouldAddOsmUploadedConflicts() {
        //  when
        statisticsRepository.addOsmUploadConflicts(1);
        statisticsRepository.addOsmUploadConflicts(0);

        //  then
        assertThat(statisticsRepository.getOsmUploadedConflicts().get())
                .isEqualTo(1);
    }
}
