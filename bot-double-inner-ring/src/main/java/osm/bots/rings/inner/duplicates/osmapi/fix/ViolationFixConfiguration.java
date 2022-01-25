package osm.bots.rings.inner.duplicates.osmapi.fix;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ViolationFixConfiguration {

    @Bean
    ReplaceRelationMemberFixGenerator replaceRelationMemberFixGenerator() {
        return new ReplaceRelationMemberFixGenerator();
    }

    @Bean
    ReplaceWayTagsFixGenerator replaceWayTagsFixGenerator() {
        return new ReplaceWayTagsFixGenerator();
    }
}
