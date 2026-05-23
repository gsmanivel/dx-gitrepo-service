package com.dx.gitrepo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration for HTTP client beans.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate bean for GitHub API calls.
     *
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
