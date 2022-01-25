package osm.bots.rings.inner.duplicates.osmose;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InnerPolygonOsmoseViolation {

    @JsonProperty("item")
    int osmoseRuleId;
    @JsonProperty("osm_ids")
    ViolatingOsmIds violatingOsmIds;

    public Long getViolatingRelationId() {
        return violatingOsmIds.getViolatingRelationId();
    }

    public List<Long> getPairOfViolatingWaysIds() {
        return violatingOsmIds.getPairOfViolatingWaysIds();
    }

    public List<Long> getViolatingWaysIds() {
        return violatingOsmIds.getWayIds();
    }

    public List<Long> getViolatingRelationsIds() {
        return violatingOsmIds.getRelationIds();
    }
}
