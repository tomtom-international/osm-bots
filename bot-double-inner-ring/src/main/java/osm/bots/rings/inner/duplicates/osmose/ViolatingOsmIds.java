package osm.bots.rings.inner.duplicates.osmose;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;

@NonFinal
@Value
public class ViolatingOsmIds {

    @JsonProperty("relations")
    List<Long> relationIds;
    @JsonProperty("ways")
    List<Long> wayIds;

    public Long getViolatingRelationId() {
        if (relationIds.size() != 1) {
            throw new IllegalArgumentException("Amount of violating relation ids is different than 1");
        }
        return relationIds.get(0);
    }

    public List<Long> getPairOfViolatingWaysIds() {
        if (wayIds.size() != 2) {
            throw new IllegalArgumentException("Amount of violating way ids is different than 2");
        }
        return wayIds;
    }
}
