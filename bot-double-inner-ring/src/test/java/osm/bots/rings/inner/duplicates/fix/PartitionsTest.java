package osm.bots.rings.inner.duplicates.fix;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class PartitionsTest {

    @Test
    void shouldPartitionBySize() {
        // given
        Collection<String> testData = createTestData();

        // when
        Partitions<String> partitions = Partitions.partitionBySize(testData, 7);

        // then
        assertThat(partitions.getViolationsPartitions())
                .extracting(List::size)
                .containsExactly(7, 7, 6);
    }

    private Collection<String> createTestData() {
        return IntStream.range(0, 20)
                .mapToObj(index -> String.format("element %s", index))
                .collect(Collectors.toList());
    }
}
