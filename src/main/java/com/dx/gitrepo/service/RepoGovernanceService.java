package com.dx.gitrepo.service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dx.gitrepo.client.GitHubApiClient;
import com.dx.gitrepo.model.GitHubRepo;
import com.dx.gitrepo.model.PullRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for GitHub repository governance operations.
 * Enforces DX org standards for repos, PRs, and commits.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepoGovernanceService {

    /** Valid DX repo naming pattern: dx-<name>-service | dx-<name>-lib | dx-<name>-model. */
    private static final Pattern VALID_REPO_NAME = Pattern.compile(
            "^dx-[a-z0-9-]+-(?:service|lib|model)$"
    );

    /** Valid commit message pattern: COMC-123: <message>. */
    private static final Pattern VALID_COMMIT_MSG = Pattern.compile(
            "^DX-\\d+:\\s.+"
    );

    /** Default hours threshold when not specified. */
    private static final int DEFAULT_HOURS = 1;

    private final GitHubApiClient gitHubApiClient;

    /**
     * Returns all repositories in the org.
     *
     * @return list of all repos
     */
    public List<GitHubRepo> getAllRepos() {
        return gitHubApiClient.getAllRepos();
    }


    /**
     * Returns open PRs across all repos that have been open longer than the given hours.
     *
     * @param hours number of hours threshold (must be positive)
     * @return list of PRs open longer than specified hours
     */
    public List<PullRequest> getOpenPrsOlderThan(final int hours) {
        final int effectiveHours = hours <= 0 ? DEFAULT_HOURS : hours;
        final OffsetDateTime threshold = OffsetDateTime.now().minus(effectiveHours, ChronoUnit.HOURS);

        return gitHubApiClient.getAllRepos().stream()
                .flatMap(repo -> gitHubApiClient.getOpenPullRequests(repo.getName()).stream())
                .filter(pr -> {
                    final OffsetDateTime createdAt = OffsetDateTime.parse(pr.getCreatedAt());
                    return createdAt.isBefore(threshold);
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns PRs with cycle time (open to close duration) for all repos.
     *
     * @return list of closed PRs with cycle time info
     */
    public List<PrCycleTime> getPrCycleTimes() {
        return gitHubApiClient.getAllRepos().stream()
                .flatMap(repo -> gitHubApiClient.getClosedPullRequests(repo.getName()).stream()
                        .filter(pr -> pr.getClosedAt() != null)
                        .map(pr -> {
                            final OffsetDateTime opened = OffsetDateTime.parse(pr.getCreatedAt());
                            final OffsetDateTime closed = OffsetDateTime.parse(pr.getClosedAt());
                            final long hours = ChronoUnit.HOURS.between(opened, closed);
                            return new PrCycleTime(repo.getName(), pr.getNumber(), pr.getTitle(), hours);
                        })
                )
                .collect(Collectors.toList());
    }

    /**
     * Deletes a repository from the org.
     *
     * @param repoName repository name to delete
     */
    public void deleteRepo(final String repoName) {
        log.warn("Deleting repository: {}", repoName);
        gitHubApiClient.deleteRepo(repoName);
    }

    /**
     * Checks whether a commit message follows DX standards (COMC-123: message).
     *
     * @param message commit message to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidCommitMessage(final String message) {
        return VALID_COMMIT_MSG.matcher(message).matches();
    }

    /**
     * Represents PR cycle time data.
     */
    public record PrCycleTime(
            String repoName,
            Integer prNumber,
            String prTitle,
            long cycleTimeHours
    ) { }

}
