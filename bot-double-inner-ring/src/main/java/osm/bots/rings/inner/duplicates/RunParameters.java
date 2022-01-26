package osm.bots.rings.inner.duplicates;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
@Configuration
@ConfigurationProperties(prefix = "run.parameters")
@With(value = AccessLevel.PACKAGE)
public class RunParameters {

    static final int MIN_VIOLATIONS_PER_CHANGESET = 1;
    static final int MAX_VIOLATIONS_PER_CHANGESET = 200;
    static final String MISSING_PATH_MESSAGE = "Path to osmose violations json is required";
    static final String MISSING_DISCUSSION_PAGE_MESSAGE = "Link to discussion page is required";
    static final String MISSING_WIKI_LINK_MESSAGE = "Link to wiki page is required";
    static final String MISSING_OPEN_STREET_MAP_API_PARAMETERS = "Open Street Map properties are required";
    static final String MAX_VIOLATIONS_PER_CHANGESET_TOO_LOW_MESSAGE = "Max violations per changeset should be higher than " + MIN_VIOLATIONS_PER_CHANGESET;
    static final String MAX_VIOLATIONS_PER_CHANGESET_TOO_HIGH_MESSAGE = "Max violations per changeset should be lower than " + MAX_VIOLATIONS_PER_CHANGESET;
    static final String MISSING_VIOLATIONS_PER_CHANGESET_MESSAGE = "Please provide max amount of violations per changeset";

    @NotNull(message = MISSING_PATH_MESSAGE)
    private Path pathToViolationsFile;
    @NotBlank(message = MISSING_DISCUSSION_PAGE_MESSAGE)
    private String osmDiscussionPage;
    @NotBlank(message = MISSING_WIKI_LINK_MESSAGE)
    private String osmWikiDocumentationPage;
    private boolean uploadFixes;
    @NotNull(message = MISSING_OPEN_STREET_MAP_API_PARAMETERS)
    @Valid
    private OpenStreetMapApiParameters openStreetMapApi;
    @Min(value = MIN_VIOLATIONS_PER_CHANGESET,
            message = MAX_VIOLATIONS_PER_CHANGESET_TOO_LOW_MESSAGE)
    @Max(value = MAX_VIOLATIONS_PER_CHANGESET,
            message = MAX_VIOLATIONS_PER_CHANGESET_TOO_HIGH_MESSAGE)
    @NotNull(message = MISSING_VIOLATIONS_PER_CHANGESET_MESSAGE)
    private Integer maxViolationsPerChangeset;
}
