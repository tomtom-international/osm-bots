package osm.bots.rings.inner.duplicates.osmapi.fetch;

import de.westnordost.osmapi.map.data.Relation;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import osm.bots.rings.inner.duplicates.osmapi.model.WayWithParentRelations;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OsmDataCompletenessVerifierTest {

    public static Stream<Arguments> getTestCases() {
        Relation relation = TestFeatureGenerator.relation()
                .id(1L)
                .build();
        WayWithParentRelations wayWithParentRelations = TestFeatureGenerator.wayWithParentRelations()
                .wayId(1L)
                .build();

        return Stream.of(
                testCase().description("OsmData should be complete")
                        .relation(relation)
                        .wayWithParentRelations(wayWithParentRelations)
                        .wayWithParentRelations(wayWithParentRelations)
                        .isNotComplete(false)
                        .build(),
                testCase().description("OsmData having null relation should be incomplete")
                        .relation(null)
                        .wayWithParentRelations(wayWithParentRelations)
                        .wayWithParentRelations(wayWithParentRelations)
                        .isNotComplete(true)
                        .build(),
                testCase().description("OsmData having relation and null ways with parent relations should be incomplete")
                        .relation(relation)
                        .wayWithParentRelations(null)
                        .isNotComplete(true)
                        .build(),
                testCase().description("OsmData having relation and 3 ways with parent relations should be incomplete")
                        .relation(relation)
                        .wayWithParentRelations(wayWithParentRelations)
                        .wayWithParentRelations(wayWithParentRelations)
                        .wayWithParentRelations(wayWithParentRelations)
                        .isNotComplete(true)
                        .build(),
                testCase().description("OsmData having relation and 2 ways where one is null should be incomplete")
                        .relation(relation)
                        .wayWithParentRelations(wayWithParentRelations)
                        .wayWithParentRelations(null)
                        .isNotComplete(true)
                        .build()
        );
    }

    @Builder(builderMethodName = "testCase", builderClassName = "ParametersGenerator")
    private static Arguments getTestCases(
            @NonNull String description,
            Relation relation,
            @Singular("wayWithParentRelations") List<WayWithParentRelations> waysWithParentRelations,
            @NonNull Boolean isNotComplete) {
        return Arguments.of(description, relation, waysWithParentRelations, isNotComplete);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("getTestCases")
    void shouldCheckIfOsmDataIsNotComplete(
            String description,
            Relation relation,
            List<WayWithParentRelations> waysWithParentRelations,
            boolean isNotComplete) {
        //  given
        OsmData osmData = new OsmData(relation, waysWithParentRelations);

        //  when
        boolean actual = OsmDataCompletenessVerifier.isDataIncomplete(osmData);

        //  then
        assertThat(actual).isEqualTo(isNotComplete);
    }
}
