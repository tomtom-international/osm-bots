package osm.bots.rings.inner.duplicates.utils;

import de.westnordost.osmapi.common.errors.OsmConnectionException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.RetryPolicy;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@UtilityClass
public class RetryPolicyFactory {

    private final int DELAY_SECONDS = 2;
    private final int MAX_DELAY_SECONDS = 10;
    private final int MAX_RETRIES = 5;
    private static final List<Class<? extends Throwable>> HANDLED_EXCEPTIONS = List.of(OsmConnectionException.class);

    public <T> RetryPolicy<T> getClientConnectionRetryPolicy() {
        return new RetryPolicy<T>()
                .handle(HANDLED_EXCEPTIONS)
                .onRetry(result -> log.warn("Retrying connection attempt due to: {}", result.getLastFailure().getMessage()))
                .onFailure(result -> log.warn("Connection FAILED due to: {}", result.getFailure().getMessage()))
                .onAbort(result -> log.warn("Connection ABORTED due to: {}", result.getFailure().getMessage()))
                .withBackoff(DELAY_SECONDS, MAX_DELAY_SECONDS, ChronoUnit.SECONDS)
                .withMaxAttempts(MAX_RETRIES);
    }
}
