package osm.bots.rings.inner.duplicates.osmapi.model;

import de.westnordost.osmapi.map.data.Element;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Value
public class ViolationFix {

    private static final String SINGLE_FIX_INFO_FORMAT = "%s id:%d, version:%d, changeType:%s, tags:[%s]";
    List<Element> edits;

    @Override
    public String toString() {
        return "{" + getElementDescription() + "}";
    }

    private String getElementDescription() {
        return edits.stream()
                .map(elementFix -> String.format(SINGLE_FIX_INFO_FORMAT,
                        elementFix.getType(), elementFix.getId(), elementFix.getVersion(), extractChangeType(elementFix), extractTags(elementFix.getTags())))
                .collect(Collectors.joining(", "));
    }

    private String extractChangeType(Element elementFix) {
        if (elementFix.isDeleted()) {
            return "DELETED";
        } else if (elementFix.isModified()) {
            return "MODIFIED";
        } else if (elementFix.isNew()) {
            return "NEW";
        } else {
            return "UNCHANGED";
        }
    }

    private String extractTags(Map<String, String> tags) {
        return tags
                .entrySet()
                .stream()
                .map(tag -> String.format("%s:%s", tag.getKey(), tag.getValue()))
                .collect(Collectors.joining(", "));
    }
}
