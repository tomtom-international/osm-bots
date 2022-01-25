package osm.bots.rings.inner.duplicates.utils;

import org.junit.jupiter.api.Test;
import osm.bots.rings.inner.duplicates.fix.Partitions;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class PartitionTest {

    @Test
    void shouldPartitionByCount() {
        // given
        Collection<String> testData = createTestData(16);

        // when
        Partitions<String> partitions = Partition.partitionByCount(testData, 7);

        // then
        assertThat(partitions.getViolationsPartitions())
                .extracting(List::size)
                .containsExactly(3, 3, 2, 2, 2, 2, 2);
    }

    @Test
    void shouldPartitionBySize() {
        // given
        Collection<String> testData = createTestData(16);

        // when
        Partitions<String> partitions = Partition.partitionBySize(testData, 7);

        // then
        assertThat(partitions.getViolationsPartitions())
                .extracting(List::size)
                .containsExactly(7, 7, 2);
    }

    private Collection<String> createTestData(int size) {
        return IntStream.range(0, size)
                .mapToObj(index -> String.format("element %s", index))
                .collect(Collectors.toList());
    }
}
