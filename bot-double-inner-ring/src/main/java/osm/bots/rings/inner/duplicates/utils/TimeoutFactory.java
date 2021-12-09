package osm.bots.rings.inner.duplicates.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Timeout;

import java.time.Duration;

@Slf4j
@UtilityClass
public class TimeoutFactory {

    private static final int MAX_ATTEMPT_TIME_MINUTES = 15;

    public <T> Timeout<T> getClientConnectionTimeout() {
        return Timeout.<T>of(Duration.ofMinutes(MAX_ATTEMPT_TIME_MINUTES))
                .withInterrupt(true)
                .onFailure(result -> log.error("Connection attempt to OSM timed out"));
    }
}
