package osm.bots.rings.inner.duplicates.osmapi.store;

import de.westnordost.osmapi.common.errors.OsmConflictException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import osm.bots.rings.inner.duplicates.osmapi.model.ViolationFix;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OsmApiFixUploader implements FixUploader {

    private final OsmWriteClient osmWriteClient;
    private final Map<String, String> changesetTags;

    public OsmApiFixUploader(OsmWriteClient osmWriteClient, String osmDiscussionPage, String osmWikiDocumentationPage) {
        this(osmWriteClient, Map.of(
                "comment", "Fix duplicated inner rings",
                "automatic", "yes",
                "bot", "yes",
                "osm_discussion_page", osmDiscussionPage,
                "osm_wiki_documentation_page", osmWikiDocumentationPage));
    }

    @Override
    public void uploadFixesInSingleChangeset(List<ViolationFix> violationFixes) {
        log.info("Uploading changes to OSM for {} violations", violationFixes.size());
        long changesetId = osmWriteClient.openChangeset(changesetTags);
        log.info("Changeset id {} has been opened", changesetId);

        violationFixes.forEach(change -> uploadChange(change, changesetId));
        osmWriteClient.closeChangeset(changesetId);
        log.info("Changeset id {} has been closed", changesetId);
    }

    private void uploadChange(ViolationFix violationFix, long changesetId) {
        try {
            osmWriteClient.uploadChanges(changesetId, violationFix.getEdits());
            log.info("Uploading: {} to changeset id:{}", violationFix, changesetId);
        } catch (OsmConflictException e) {
            log.error("Skipping fix of conflicting data: {} in changeset: {}. Exception details: {}", violationFix, changesetId, e);
        }
    }
}
