package osm.bots.rings.inner.duplicates.utils;

import lombok.experimental.UtilityClass;
import osm.bots.rings.inner.duplicates.fix.Partitions;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@UtilityClass
public class Partition {

    public <T> Partitions<T> partitionByCount(Iterable<T> elements, int partitionCount) {
        AtomicLong counter = new AtomicLong();
        return new Partitions<>(StreamSupport.stream(elements.spliterator(), false)
                .collect(Collectors.groupingBy(element -> counter.getAndIncrement() % partitionCount))
                .values());
    }

    public <T> Partitions<T> partitionBySize(Iterable<T> elements, int partitionSize) {
        AtomicLong counter = new AtomicLong();
        return new Partitions<>(
                StreamSupport.stream(elements.spliterator(), false)
                        .collect(Collectors.groupingBy(element -> counter.getAndIncrement() / partitionSize))
                        .values());
    }
}
