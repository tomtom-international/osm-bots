package osm.bots.rings.inner.duplicates.osmapi.fetch;

import org.junit.jupiter.api.Test;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolatingOsmData;
import osm.bots.rings.inner.duplicates.utils.TestFeatureGenerator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OsmDataConverterTest {

    @Test
    void shouldConvertCompleteOsmData() {
        //  given
        final long WAY_1_ID = 1L;
        final long WAY_2_ID = 2L;
        final long RELATION_ID = 1L;
        OsmData osmData = TestFetchDataGenerator.createOsmData(RELATION_ID, WAY_1_ID, WAY_2_ID);

        //  when
        Optional<ViolatingOsmData> actual = OsmDataConverter.convert(osmData);

        //  then
        assertThat(actual).isPresent();

        assertThat(actual.get())
                .usingRecursiveComparison()
                .isEqualTo(TestFetchDataGenerator.createViolatingOsmData(RELATION_ID, WAY_1_ID, WAY_2_ID));
    }

    @Test
    void shouldConvertNothingWhenBothWaysHaveParentRelations() {
        //  given
        OsmData osmData = TestFetchDataGenerator.osmData()
                .relationId(1L)
                .wayWithParentRelations(TestFeatureGenerator.wayWithParentRelations()
                        .parentRelationIds(List.of(1L))
                        .build())
                .wayWithParentRelations(TestFeatureGenerator.wayWithParentRelations()
                        .parentRelationIds(List.of(1L))
                        .build())
                .build();

        //  when
        Optional<ViolatingOsmData> actual = OsmDataConverter.convert(osmData);

        //  then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldConvertNothingWhenWaysHaveNoParentRelations() {
        //  given
        OsmData osmData = TestFetchDataGenerator.osmData()
                .relationId(1L)
                .wayWithParentRelations(TestFeatureGenerator.wayWithParentRelations()
                        .parentRelationIds(List.of())
                        .build())
                .wayWithParentRelations(TestFeatureGenerator.wayWithParentRelations()
                        .parentRelationIds(List.of())
                        .build())
                .build();

        //  when
        Optional<ViolatingOsmData> actual = OsmDataConverter.convert(osmData);

        //  then
        assertThat(actual).isEmpty();
    }
}
