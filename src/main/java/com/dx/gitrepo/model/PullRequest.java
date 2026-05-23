package com.dx.gitrepo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents a GitHub Pull Request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {

    /** PR number. */
    private Integer number;

    /** PR title. */
    private String title;

    /** PR state (open/closed). */
    private String state;

    /** PR HTML URL. */
    @JsonProperty("html_url")
    private String htmlUrl;

    /** PR creation date. */
    @JsonProperty("created_at")
    private String createdAt;

    /** PR closed date. */
    @JsonProperty("closed_at")
    private String closedAt;

    /** PR merged date. */
    @JsonProperty("merged_at")
    private String mergedAt;

    /** PR author. */
    private GitHubUser user;

    /** PR head branch. */
    private Branch head;

    /** PR base branch. */
    private Branch base;

    /**
     * Represents a GitHub user.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubUser {

        /** GitHub username. */
        private String login;

    }

    /**
     * Represents a branch in a PR.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Branch {

        /** Branch name. */
        private String ref;

    }

}
