package com.project.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    // Define a constant representing the maximum age for CORS cache in seconds.
    // Here it is set to 24 hours (24 hours * 60 minutes/hour * 60 seconds/minute)
    private static final long MAX_AGE = 24 * 60 * 60;

    // Use Spring's @Bean annotation to indicate that this method returns a Bean object managed by the Spring container
    @Bean
    public CorsFilter corsFilter() {
        // Create a URL-based CORS configuration source object
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Create a CORS configuration object
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Allow cross-origin requests from any origin
        corsConfiguration.addAllowedOrigin("*");

        // Allow all HTTP request headers
        corsConfiguration.addAllowedHeader("*");

        // Allow all HTTP request methods (e.g., GET, POST, etc.)
        corsConfiguration.addAllowedMethod("*");

        // Set the maximum age for CORS cache
        corsConfiguration.setMaxAge(MAX_AGE);

        // Register this CORS configuration for all URL paths (/** means all paths)
        source.registerCorsConfiguration("/**", corsConfiguration);

        // Create a CORS filter with the configuration source and return it
        return new CorsFilter(source);
    }

}
