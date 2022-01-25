package osm.bots.rings.inner.duplicates;

import lombok.Builder;
import lombok.Singular;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.OpenStreetMapApiParameters.OpenStreetMapApiCredential;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static osm.bots.rings.inner.duplicates.OpenStreetMapApiParameters.MISSING_API_URL_MESSAGE;
import static osm.bots.rings.inner.duplicates.OpenStreetMapApiParameters.MISSING_CONSUMER_KEY_MESSAGE;
import static osm.bots.rings.inner.duplicates.OpenStreetMapApiParameters.MISSING_CONSUMER_SECRET_MESSAGE;
import static osm.bots.rings.inner.duplicates.OpenStreetMapApiParameters.MISSING_TOKEN_MESSAGE;
import static osm.bots.rings.inner.duplicates.OpenStreetMapApiParameters.MISSING_TOKEN_SECRET_MESSAGE;
import static osm.bots.rings.inner.duplicates.RunParameters.MAX_VIOLATIONS_PER_CHANGESET_TOO_HIGH_MESSAGE;
import static osm.bots.rings.inner.duplicates.RunParameters.MAX_VIOLATIONS_PER_CHANGESET_TOO_LOW_MESSAGE;
import static osm.bots.rings.inner.duplicates.RunParameters.MISSING_DISCUSSION_PAGE_MESSAGE;
import static osm.bots.rings.inner.duplicates.RunParameters.MISSING_OPEN_STREET_MAP_API_PARAMETERS;
import static osm.bots.rings.inner.duplicates.RunParameters.MISSING_PATH_MESSAGE;
import static osm.bots.rings.inner.duplicates.RunParameters.MISSING_VIOLATIONS_PER_CHANGESET_MESSAGE;
import static osm.bots.rings.inner.duplicates.RunParameters.MISSING_WIKI_LINK_MESSAGE;

class RunParametersTest {

    private static Validator propertyValidator;

    @BeforeAll
    public static void setup() {
        propertyValidator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static Stream<Arguments> getTestCases() {
        RunParameters emptyParameters = new RunParameters();
        RunParameters completeParameters = new RunParameters(
                Path.of("dummyPath"),
                "dummyDiscussionPage",
                "dummyWikiPage",
                true,
                new OpenStreetMapApiParameters("dummyApiUrl",
                        new OpenStreetMapApiCredential(
                                "dummyToken",
                                "dummyTokenSecret",
                                "dummyKey",
                                "dummyConsumerSecret"
                        )),
                1);
        RunParameters completeParametersWithoutCredentials = completeParameters.withOpenStreetMapApi(
                new OpenStreetMapApiParameters().withUrl("anyUrl"));
        RunParameters emptyOpenStreetMapApiParameters = completeParameters.withOpenStreetMapApi(
                new OpenStreetMapApiParameters().withCredentials(new OpenStreetMapApiCredential()));

        return Stream.of(
                testCase().name("Complete parameters should pass validation")
                        .runParameters(completeParameters)
                        .expectedMessages(Set.of()),
                testCase().name("Complete parameters without credentials should pass validation")
                        .runParameters(completeParametersWithoutCredentials)
                        .expectedMessages(Set.of()),
                testCase().name("Empty parameters should fail validation for all parameters")
                        .runParameters(emptyParameters)
                        .expectedMessages(Set.of(
                                MISSING_PATH_MESSAGE,
                                MISSING_DISCUSSION_PAGE_MESSAGE,
                                MISSING_WIKI_LINK_MESSAGE,
                                MISSING_OPEN_STREET_MAP_API_PARAMETERS,
                                MISSING_VIOLATIONS_PER_CHANGESET_MESSAGE)),
                testCase().name("Parameters without API URL should fail validation")
                        .runParameters(completeParameters.withOpenStreetMapApi(new OpenStreetMapApiParameters()))
                        .expectedMessages(Set.of(MISSING_API_URL_MESSAGE)),
                testCase().name("Empty Open Street Map credential parameters should fail validation for all parameters")
                        .runParameters(emptyOpenStreetMapApiParameters)
                        .expectedMessages(Set.of(
                                MISSING_TOKEN_MESSAGE,
                                MISSING_TOKEN_SECRET_MESSAGE,
                                MISSING_CONSUMER_KEY_MESSAGE,
                                MISSING_CONSUMER_SECRET_MESSAGE,
                                MISSING_API_URL_MESSAGE)),
                testCase().name("One missing parameter should fail validation")
                        .runParameters(completeParameters.withPathToViolationsFile(null))
                        .expectedMessage(MISSING_PATH_MESSAGE),
                testCase().name("Amount of violations per changeset is too low should fail validation")
                        .runParameters(completeParameters.withMaxViolationsPerChangeset(0))
                        .expectedMessage(MAX_VIOLATIONS_PER_CHANGESET_TOO_LOW_MESSAGE),
                testCase().name("Amount of violations per changeset is too high should fail validation")
                        .runParameters(completeParameters.withMaxViolationsPerChangeset(10000))
                        .expectedMessage(MAX_VIOLATIONS_PER_CHANGESET_TOO_HIGH_MESSAGE)
        ).map(TestCaseBuilder::build);
    }

    @Builder(builderMethodName = "testCase", builderClassName = "TestCaseBuilder")
    private static Arguments buildTestCase(String name, RunParameters runParameters, @Singular Set<String> expectedMessages) {
        return Arguments.of(name, runParameters, expectedMessages);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldValidateRunParameters(String name, RunParameters runParameters, Set<String> expectedMessages) {
        Set<ConstraintViolation<RunParameters>> validationViolations = propertyValidator.validate(runParameters);

        assertThat(validationViolations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrderElementsOf(expectedMessages);
    }
}
