package com.dx.gitrepo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dx.gitrepo.model.ApiResponse;
import com.dx.gitrepo.model.GitHubRepo;
import com.dx.gitrepo.model.PullRequest;
import com.dx.gitrepo.service.RepoGovernanceService;
import com.dx.gitrepo.service.RepoGovernanceService.PrCycleTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for GitHub repository governance APIs.
 */
@Slf4j
@RestController
@RequestMapping("/repos")
@RequiredArgsConstructor
public class RepoGovernanceController {

    /** Admin role header value expected for delete operations. */
    private static final String ADMIN_ROLE = "admin";

    private final RepoGovernanceService repoGovernanceService;

    /**
     * Lists all repositories in the org.
     *
     * @return list of all repos
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<GitHubRepo>>> getAllRepos() {
        log.info("Fetching all repos");
        final List<GitHubRepo> repos = repoGovernanceService.getAllRepos();
        return ResponseEntity.ok(ApiResponse.successList(repos));
    }



    /**
     * Returns open PRs older than the specified number of hours.
     *
     * @param hours number of hours threshold (optional, default 1)
     * @return list of stale open PRs
     */
    @GetMapping("/open-prs")
    public ResponseEntity<ApiResponse<List<PullRequest>>> getOpenPrs(
            @RequestParam(required = false) final Integer hours) {

        if (hours != null && hours < 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("hours parameter must be a positive integer"));
        }

        final int effectiveHours = hours != null ? hours : 1;
        log.info("Fetching open PRs older than {} hours", effectiveHours);
        final List<PullRequest> prs = repoGovernanceService.getOpenPrsOlderThan(effectiveHours);
        return ResponseEntity.ok(ApiResponse.successList(prs));
    }

    /**
     * Returns PR cycle time for all repos.
     *
     * @return list of PRs with cycle time in hours
     */
    @GetMapping("/pr-cycle-time")
    public ResponseEntity<ApiResponse<List<PrCycleTime>>> getPrCycleTimes() {
        log.info("Fetching PR cycle times");
        final List<PrCycleTime> cycleTimes = repoGovernanceService.getPrCycleTimes();
        return ResponseEntity.ok(ApiResponse.successList(cycleTimes));
    }

    /**
     * Validates a commit message against DX standards (COMC-123: message).
     *
     * @param message commit message to validate
     * @return validation result
     */
    @GetMapping("/commit-violations")
    public ResponseEntity<ApiResponse<Boolean>> validateCommitMessage(
            @RequestParam final String message) {
        log.info("Validating commit message");
        final boolean isValid = repoGovernanceService.isValidCommitMessage(message);
        final String msg = isValid
                ? "Commit message is valid"
                : "Commit message violates DX standard (expected: COMC-123: <message>)";
        return ResponseEntity.ok(ApiResponse.of("success", msg, isValid));
    }

    /**
     * Deletes a repository. Restricted to GitHub org admins only.
     *
     * @param repoName repository name to delete
     * @param role     caller role from request header
     * @return 204 on success, 403 if not admin
     */
    @DeleteMapping("/{repoName}")
    public ResponseEntity<ApiResponse<Void>> deleteRepo(
            @PathVariable final String repoName,
            @RequestHeader(value = "X-User-Role", required = false) final String role) {

        if (!ADMIN_ROLE.equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("Only admins can delete repositories"));
        }

        log.warn("Admin requested deletion of repo: {}", repoName);
        repoGovernanceService.deleteRepo(repoName);
        return ResponseEntity.noContent().build();
    }

}
