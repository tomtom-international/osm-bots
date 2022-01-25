package osm.bots.rings.inner.duplicates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import osm.bots.rings.inner.duplicates.osmapi.fetch.OsmDataClient;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;
import osm.bots.rings.inner.duplicates.osmapi.store.FixUploader;
import osm.bots.rings.inner.duplicates.osmose.InnerPolygonOsmoseViolation;
import osm.bots.rings.inner.duplicates.osmose.OsmoseViolationsReader;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static osm.bots.rings.inner.duplicates.AbstractIntegrationTest.IntegrationTestConfiguration;
import static osm.bots.rings.inner.duplicates.Assertions.UploadedChangesetsAssert;
import static osm.bots.rings.inner.duplicates.Assertions.assertThat;

@SpringBootTest(classes = IntegrationTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@IntegrationTest
abstract class AbstractIntegrationTest {

    @MockBean
    protected OsmoseViolationsReader violationJsonReader;
    @MockBean
    protected OsmDataClient osmDataClient;
    @SpyBean
    protected FixUploader fixUploader;
    @Autowired
    private ViolationsFixer violationsFixer;
    @Autowired
    private RunParameters runParameters;
    private final List<List<ViolationFix>> interceptedChangesets = new ArrayList<>();

    protected abstract List<TestViolation> givenViolations();

    protected abstract TestOsmData givenOsmData();

    protected abstract void thenValidateUploadedChangesets(UploadedChangesetsAssert anAssert);

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            interceptedChangesets.add(invocation.getArgument(0));
            invocation.callRealMethod();
            return null;
        }).when(fixUploader).uploadFixesInSingleChangeset(any());
    }

    @Test
    void shouldRunViolationFixerAndPrepareFixes() {
        //given
        prepareViolations(givenViolations());
        prepareOsmData(givenOsmData());
        //when
        violationsFixer.fixViolations(runParameters.getPathToViolationsFile());
        //then
        thenValidateUploadedChangesets(assertThat(interceptedChangesets));
    }

    private void prepareOsmData(final TestOsmData osmData) {
        osmData.getWays().forEach(
                way -> when(osmDataClient.getWay(way.getId())).thenReturn(way)
        );
        osmData.getRelations().forEach(
                relation -> when(osmDataClient.getRelation(relation.getId())).thenReturn(relation)
        );
        osmData.getWaysRelations().forEach(
                (key, value) -> when(osmDataClient.getRelationsForWay(key)).thenReturn(value));
    }

    private void prepareViolations(final List<TestViolation> testViolations) {
        final List<InnerPolygonOsmoseViolation> violations = testViolations.stream()
                .map(TestViolation::toDuplicatedInnerPolygonViolation)
                .collect(toList());
        when(violationJsonReader.read(any())).thenReturn(violations);
    }

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan(
            excludeFilters = @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = InnerRingApplication.class)
    )
    static class IntegrationTestConfiguration {

    }
}
