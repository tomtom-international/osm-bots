package osm.bots.rings.inner.duplicates.statistics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class StatisticsConfiguration {

    @Bean
    StatisticsRepository statisticsRepository() {
        return new StatisticsRepository();
    }
}
