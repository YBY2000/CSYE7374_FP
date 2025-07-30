package com.example.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Use Spring's @Bean annotation to indicate that this method returns a Bean object managed by the Spring container
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // Allow cross-origin requests from any origin
        corsConfiguration.addAllowedHeader("*"); // Allow all HTTP request headers
        corsConfiguration.addAllowedMethod("*"); // Allow all HTTP request methods (e.g., GET, POST, etc.)
        source.registerCorsConfiguration("/**", corsConfiguration); // Register this CORS configuration for all URL paths (/** means all paths)
        return new CorsFilter(source);
    }
}