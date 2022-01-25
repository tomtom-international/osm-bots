package osm.bots.rings.inner.duplicates.fix;

import lombok.Value;

import java.util.Collection;
import java.util.List;

@Value
public class Partitions<T> {

    Collection<List<T>> violationsPartitions;
}
