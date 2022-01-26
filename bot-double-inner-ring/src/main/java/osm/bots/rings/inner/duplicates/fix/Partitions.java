package osm.bots.rings.inner.duplicates.fix;

import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Value
public class Partitions<T> {

    Collection<List<T>> violationsPartitions;

    public static <T> Partitions<T> partitionBySize(Iterable<T> elements, int partitionSize) {
        AtomicLong counter = new AtomicLong();
        return new Partitions<>(
                StreamSupport.stream(elements.spliterator(), false)
                        .collect(Collectors.groupingBy(element -> counter.getAndIncrement() / partitionSize))
                        .values());
    }
}
