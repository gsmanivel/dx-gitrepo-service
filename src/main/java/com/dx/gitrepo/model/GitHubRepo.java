package com.dx.gitrepo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents a GitHub repository.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepo {

    /** Repository ID. */
    private Long id;

    /** Repository name. */
    private String name;

    /** Full repository name including owner (owner/repo). */
    @JsonProperty("full_name")
    private String fullName;

    /** Repository description. */
    private String description;

    /** Whether the repository is private. */
    @JsonProperty("private")
    private boolean privateRepo;

    /** Repository HTML URL. */
    @JsonProperty("html_url")
    private String htmlUrl;

    /** Repository creation date. */
    @JsonProperty("created_at")
    private String createdAt;

    /** Repository last updated date. */
    @JsonProperty("updated_at")
    private String updatedAt;

    /** Default branch name. */
    @JsonProperty("default_branch")
    private String defaultBranch;

}
