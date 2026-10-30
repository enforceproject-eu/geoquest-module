package org.n52.project.enforce.geoquest.api;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoQuestWebConfig {

    @Bean
    GroupedOpenApi geoQuestApis() {
        return GroupedOpenApi.builder().group("geoquest_data").pathsToMatch("/geoquest_data/**").build();
    }

    @Bean
    GroupedOpenApi tokenApis() {
        return GroupedOpenApi.builder().group("geoquest_token").pathsToMatch("/token/**").build();
    }

}
