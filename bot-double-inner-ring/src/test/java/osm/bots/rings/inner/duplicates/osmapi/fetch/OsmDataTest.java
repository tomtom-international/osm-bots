package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.data.Relation;
import lombok.Builder;
import lombok.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

class OsmDataTest {

    private static Stream<Arguments> getTestCases() {
        String NULL = "null";
        Relation relation = TestFeatureGenerator.relation()
                .id(1L)
                .build();
        WayWithParentRelations way1WithParentRelations = TestFeatureGenerator.wayWithParentRelations()
                .wayId(1L)
                .build();
        WayWithParentRelations way2WithParentRelations = TestFeatureGenerator.wayWithParentRelations()
                .wayId(2L)
                .build();
        return Stream.of(
                osmDataTestCase()
                        .description("For null values should log only null")
                        .expectedRelationId(NULL)
                        .expectedWayIds(NULL)
                        .build(),
                osmDataTestCase()
                        .description("For relation set to null should log only way ids")
                        .waysWithParentRelationsList(List.of(way1WithParentRelations, way2WithParentRelations))
                        .expectedRelationId(NULL)
                        .expectedWayIds("1, 2")
                        .build(),
                osmDataTestCase()
                        .description("For ways with parent relations set to null should log only relation id")
                        .relation(relation)
                        .expectedRelationId("1")
                        .expectedWayIds(NULL)
                        .build()
        );
    }

    @Builder(builderMethodName = "osmDataTestCase", builderClassName = "OsmDataBuilder")
    private static Arguments testCase(
            @NonNull String description,
            Relation relation,
            String expectedRelationId,
            List<WayWithParentRelations> waysWithParentRelationsList,
            String expectedWayIds) {
        return Arguments.of(description, relation, expectedRelationId, waysWithParentRelationsList, expectedWayIds);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldVerifyLogMessage(
            String description,
            Relation relation,
            String expectedRelationId,
            List<WayWithParentRelations> waysWithParentRelations,
            String expectedWayIds) {
        //  given
        OsmData osmData = new OsmData(relation, waysWithParentRelations);

        //  then
        assertThat(osmData)
                .hasToString(format("{relation: %s wayIds: %s}", expectedRelationId, expectedWayIds));
    }
}
