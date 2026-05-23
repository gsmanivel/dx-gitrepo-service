package com.dx.gitrepo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import com.dx.gitrepo.model.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for all REST controllers.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles GitHub API client errors (4xx responses).
     *
     * @param ex the HTTP client error exception
     * @return error response with appropriate status
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponse<Void>> handleGitHubClientError(final HttpClientErrorException ex) {
        log.error("GitHub API error: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.error("GitHub API error: " + ex.getMessage()));
    }

    /**
     * Handles all unexpected exceptions.
     *
     * @param ex the exception
     * @return 500 internal server error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericError(final Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error: " + ex.getMessage()));
    }

}
