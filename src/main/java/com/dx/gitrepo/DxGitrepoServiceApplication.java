package com.dx.gitrepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for dx-gitrepo-service.
 * Provides GitHub repository governance APIs for the DX organization.
 */
@SpringBootApplication
public class DxGitrepoServiceApplication {

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(DxGitrepoServiceApplication.class, args);
    }

}
