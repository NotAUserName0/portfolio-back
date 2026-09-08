package com.portfolio.porfolio.config;

import com.portfolio.porfolio.utils.Components.JwtAuthFilter;
import com.portfolio.porfolio.utils.Components.RateLimitingFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @description Security configuration for the application.
 * SecurityConfig class defines the security settings for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimitingFilter rateLimitingFilter;

    /**
     * @description Injects required security filters.
     * @param jwtAuthFilter Filter for validating JWT authentication tokens.
     * @param rateLimitingFilter Filter for mitigating DDoS and brute-force attacks via rate limiting.
     */
    SecurityConfig(
        JwtAuthFilter jwtAuthFilter,
        RateLimitingFilter rateLimitingFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    /**
     * @description Configures the security filter chain for the application.
     *              - Enables CORS with dedicated configuration source.
     *              - Disables CSRF as this is a stateless REST API using Bearer tokens.
     *              - Adds HTTP security headers (X-Frame-Options: DENY, X-Content-Type-Options: nosniff).
     *              - Permits public access to health check, portfolio reading, uploads, and auth.
     *              - Registers RateLimitingFilter before JWT filter to block brute-force attempts early.
     * @param http the HttpSecurity object used to configure security settings
     * @param corsConfigurationSource the CORS configuration source bean
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        @Qualifier("getCorsConfigurationSource") CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable()) // Stateless REST API: JWT tokens in headers eliminate CSRF vector
            .headers(headers -> headers
                // Prevent Clickjacking attacks by disallowing iframing
                .frameOptions(frame -> frame.deny())
                // Prevent MIME-type sniffing (X-Content-Type-Options: nosniff)
                .contentTypeOptions(contentType -> {})
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/liveness").permitAll()
                .requestMatchers("/getPortfolio").permitAll()
                // Explicitly permit access to uploaded static images so frontend can render them
                .requestMatchers("/upload/**").permitAll()
                .requestMatchers("/auth", "/auth/**").permitAll()
                // Permit error dispatch so missing resources return 404 instead of 403
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
            ).sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless: no HTTP session stored on server
            )
            // Rate limiting filter executes first to mitigate DDoS/brute-force attacks
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            // JWT authentication filter validates tokens for protected endpoints
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * @description Provides the AuthenticationManager bean for the application.
     * @param config the AuthenticationConfiguration used to obtain the AuthenticationManager
     * @return the AuthenticationManager bean
     * @throws Exception if an error occurs while creating the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}