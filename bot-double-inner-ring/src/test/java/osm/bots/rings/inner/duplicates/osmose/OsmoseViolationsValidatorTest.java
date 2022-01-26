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
        InnerPolygonOsmoseViolation invalidViolationTooManyRelations = new InnerPolygonOsmoseViolation(
                DUPLICATE_RING_RULE_ID,
                new ViolatingOsmIds(List.of(1L, 2L), List.of(1L, 2L)));
        InnerPolygonOsmoseViolation invalidViolationTooManyWays = new InnerPolygonOsmoseViolation(
                DUPLICATE_RING_RULE_ID,
                new ViolatingOsmIds(List.of(1L), List.of(1L, 2L, 3L)));
        InnerPolygonOsmoseViolation invalidViolationRuleId = new InnerPolygonOsmoseViolation(
                DIFFERENT_RULE_ID,
                new ViolatingOsmIds(List.of(1L), List.of(1L, 2L)));
        InnerPolygonOsmoseViolation invalidViolationTooFewFeatures = new InnerPolygonOsmoseViolation(
                DUPLICATE_RING_RULE_ID,
                new ViolatingOsmIds(List.of(), List.of(1L)));

        InnerPolygonOsmoseViolation validViolation = new InnerPolygonOsmoseViolation(
                DUPLICATE_RING_RULE_ID,
                new ViolatingOsmIds(List.of(1L), List.of(1L, 2L)));

        //when
        List<InnerPolygonOsmoseViolation> actualValidatedViolations = osmoseViolationsValidator.getValidViolations(
                List.of(invalidViolationTooManyRelations,
                        invalidViolationTooManyWays,
                        validViolation,
                        invalidViolationRuleId,
                        invalidViolationTooFewFeatures));

        //then
        Assertions.assertThat(actualValidatedViolations).isEqualTo(List.of(validViolation));
    }
}
