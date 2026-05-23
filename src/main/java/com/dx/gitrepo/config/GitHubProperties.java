package com.dx.gitrepo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * GitHub configuration properties loaded from application.yml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "github")
public class GitHubProperties {

    /** GitHub personal access token or app token. */
    private String token;

    /** GitHub organization name. */
    private String org;

    /** GitHub API base URL. */
    private String apiUrl;

}
