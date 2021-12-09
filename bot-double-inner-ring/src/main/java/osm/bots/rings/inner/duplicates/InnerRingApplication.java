package osm.bots.rings.inner.duplicates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class InnerRingApplication implements CommandLineRunner {

    private final ViolationsFixer violationsFixer;
    private final RunParameters runParameters;

    public static void main(String[] args) {
        SpringApplication.run(InnerRingApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Running double inner ring fixer for following arguments {}", args);

        violationsFixer.fixViolations(runParameters.getPathToViolationsFile());
        log.info("BOT processing has been finished");
    }
}
