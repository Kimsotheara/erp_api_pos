package com.theara.erp.config;

import com.theara.erp.exception.RestAccessDeniedHandler;
import com.theara.erp.exception.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Stateless security for the ERP API.
 *
 * <p>Behaviour is controlled by {@code erp.security.enabled}:
 * <ul>
 *   <li>{@code false} (default for local dev) — all endpoints are open so Swagger
 *       can be explored without a token.</li>
 *   <li>{@code true} — every endpoint except Swagger/actuator requires a valid
 *       OAuth2 JWT (configure {@code spring.security.oauth2.resourceserver.jwt.issuer-uri}).</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/api/v1/auth/login",
            "/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**",
            "/actuator/health", "/actuator/info"
    };

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler,
            @org.springframework.beans.factory.annotation.Value("${erp.security.enabled:false}") boolean securityEnabled
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Standard envelope for 401 (no/invalid token) and 403 (insufficient authority).
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        if (securityEnabled) {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(PUBLIC_PATHS).permitAll()
                            .anyRequest().authenticated())
                    .oauth2ResourceServer(oauth -> oauth
                            .authenticationEntryPoint(authenticationEntryPoint)
                            .jwt(Customizer.withDefaults()));
        } else {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        return http.build();
    }

    /** Hashes the local-password fallback credentials stored in {@code users.password_hash}. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
