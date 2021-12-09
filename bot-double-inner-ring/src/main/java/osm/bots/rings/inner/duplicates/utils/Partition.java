package osm.bots.rings.inner.duplicates.utils;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@UtilityClass
public class Partition {

    public <T> Collection<List<T>> partitionByCount(Iterable<T> elements, int partitionCount) {
        AtomicLong counter = new AtomicLong();
        return StreamSupport.stream(elements.spliterator(), false)
                .collect(Collectors.groupingBy(element -> counter.getAndIncrement() % partitionCount))
                .values();
    }

    public <T> Collection<List<T>> partitionBySize(Iterable<T> elements, int partitionSize) {
        AtomicLong counter = new AtomicLong();
        return StreamSupport.stream(elements.spliterator(), false)
                .collect(Collectors.groupingBy(element -> counter.getAndIncrement() / partitionSize))
                .values();
    }
}
