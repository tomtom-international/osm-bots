package osm.bots.rings.inner.duplicates.osmose;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class OsmoseViolationsValidatorTest {

    private static final int DUPLICATE_RING_RULE_ID = 1170;
    private static final int DIFFERENT_RULE_ID = 1;
    private OsmoseViolationsValidator osmoseViolationsValidator;

    @BeforeEach
    void setUp() {
        osmoseViolationsValidator = new OsmoseViolationsValidator();
    }

    @Test
    void shouldFilterOutInvalidViolations() {
        //given
        DuplicatedInnerPolygonViolation invalidViolationTooManyRelations = new DuplicatedInnerPolygonViolation(
                DUPLICATE_RING_RULE_ID,
                new ViolatingOsmIds(List.of(1L, 2L), List.of(1L, 2L)));
        DuplicatedInnerPolygonViolation invalidViolationTooManyWays = new DuplicatedInnerPolygonViolation(
                DUPLICATE_RING_RULE_ID,
                new ViolatingOsmIds(List.of(1L), List.of(1L, 2L, 3L)));
        DuplicatedInnerPolygonViolation invalidViolationRuleId = new DuplicatedInnerPolygonViolation(
                DIFFERENT_RULE_ID,
                new ViolatingOsmIds(List.of(1L), List.of(1L, 2L)));
        DuplicatedInnerPolygonViolation invalidViolationTooFewFeatures = new DuplicatedInnerPolygonViolation(
                DUPLICATE_RING_RULE_ID,
                new ViolatingOsmIds(List.of(), List.of(1L)));

        DuplicatedInnerPolygonViolation validViolation = new DuplicatedInnerPolygonViolation(
                DUPLICATE_RING_RULE_ID,
                new ViolatingOsmIds(List.of(1L), List.of(1L, 2L)));

        //when
        List<DuplicatedInnerPolygonViolation> actualValidatedViolations = osmoseViolationsValidator.getValidViolations(
                List.of(invalidViolationTooManyRelations,
                        invalidViolationTooManyWays,
                        validViolation,
                        invalidViolationRuleId,
                        invalidViolationTooFewFeatures));

        //then
        Assertions.assertThat(actualValidatedViolations).isEqualTo(List.of(validViolation));
    }
}
