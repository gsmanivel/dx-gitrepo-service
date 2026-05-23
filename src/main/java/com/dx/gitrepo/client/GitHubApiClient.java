package com.dx.gitrepo.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.dx.gitrepo.config.GitHubProperties;
import com.dx.gitrepo.model.GitHubRepo;
import com.dx.gitrepo.model.PullRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP client for GitHub REST API interactions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubApiClient {

    /** GitHub API accept header value. */
    private static final String GITHUB_ACCEPT = "application/vnd.github+json";

    /** GitHub API version header key. */
    private static final String API_VERSION_HEADER = "X-GitHub-Api-Version";

    /** GitHub API version. */
    private static final String API_VERSION = "2022-11-28";

    /** Max repos per page (GitHub max). */
    private static final int PER_PAGE = 100;

    private final RestTemplate restTemplate;
    private final GitHubProperties gitHubProperties;

    /**
     * Fetches all repositories for the configured org.
     *
     * @return list of GitHub repositories
     */
    public List<GitHubRepo> getAllRepos() {
        final List<GitHubRepo> allRepos = new ArrayList<>();
        int page = 1;
        GitHubRepo[] repos;

        do {
            final String url = String.format(
                    "%s/users/%s/repos?per_page=%d&page=%d",
                    gitHubProperties.getApiUrl(),
                    gitHubProperties.getOrg(),
                    PER_PAGE,
                    page
            );
            repos = exchange(url, GitHubRepo[].class);
            if (repos != null && repos.length > 0) {
                allRepos.addAll(Arrays.asList(repos));
            }
            page++;
        } while (repos != null && repos.length == PER_PAGE);

        log.debug("Fetched {} repos for org {}", allRepos.size(), gitHubProperties.getOrg());
        return allRepos;
    }

    /**
     * Fetches open pull requests for a given repository.
     *
     * @param repoName repository name (without org prefix)
     * @return list of open pull requests
     */
    public List<PullRequest> getOpenPullRequests(final String repoName) {
        final String url = String.format(
                "%s/repos/%s/%s/pulls?state=open&per_page=%d",
                gitHubProperties.getApiUrl(),
                gitHubProperties.getOrg(),
                repoName,
                PER_PAGE
        );
        final PullRequest[] prs = exchange(url, PullRequest[].class);
        return prs != null ? Arrays.asList(prs) : new ArrayList<>();
    }

    /**
     * Fetches closed pull requests for a given repository.
     *
     * @param repoName repository name
     * @return list of closed pull requests
     */
    public List<PullRequest> getClosedPullRequests(final String repoName) {
        final String url = String.format(
                "%s/repos/%s/%s/pulls?state=closed&per_page=%d",
                gitHubProperties.getApiUrl(),
                gitHubProperties.getOrg(),
                repoName,
                PER_PAGE
        );
        final PullRequest[] prs = exchange(url, PullRequest[].class);
        return prs != null ? Arrays.asList(prs) : new ArrayList<>();
    }

    /**
     * Deletes a repository. Requires admin token.
     *
     * @param repoName repository name to delete
     */
    public void deleteRepo(final String repoName) {
        final String url = String.format(
                "%s/repos/%s/%s",
                gitHubProperties.getApiUrl(),
                gitHubProperties.getOrg(),
                repoName
        );
        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(buildHeaders()), Void.class);
        log.info("Deleted repository: {}/{}", gitHubProperties.getOrg(), repoName);
    }

    /**
     * Generic GET exchange with GitHub auth headers.
     *
     * @param <T>           response type
     * @param url           full API URL
     * @param responseType  class of response
     * @return response body or null
     */
    private <T> T exchange(final String url, final Class<T> responseType) {
        final ResponseEntity<T> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), responseType
        );
        return response.getBody();
    }

    /**
     * Builds HTTP headers with GitHub auth and accept headers.
     *
     * @return HttpHeaders with auth and accept set
     */
    private HttpHeaders buildHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + gitHubProperties.getToken());
        headers.set("Accept", GITHUB_ACCEPT);
        headers.set(API_VERSION_HEADER, API_VERSION);
        return headers;
    }

}
