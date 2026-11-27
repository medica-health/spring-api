package com.cdac.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * HTTP security for the auth service.
 *
 * <p><b>Current state (skeleton):</b> stateless, CSRF off, CORS from {@code CorsConfig},
 * and every route permitted, because no protected endpoints exist yet. This keeps
 * {@code /}, {@code /api/health} and the actuator probes working unchanged.
 *
 * <p><b>TODO, when the auth endpoints are implemented:</b>
 * <ul>
 *   <li>Permit: {@code /}, {@code /api/health}, {@code /actuator/health/**}, {@code /error},
 *       {@code POST /api/auth/register|login|refresh|logout|forgot-password|reset-password|verify-email}.</li>
 *   <li>Authenticated: {@code GET /api/auth/me}, {@code POST /api/auth/change-password}.</li>
 *   <li>{@code hasRole("ADMIN")}: {@code /api/admin/**}.</li>
 *   <li>{@code .oauth2ResourceServer(o -> o.jwt(...))} using the {@code JwtDecoder} from
 *       {@link JwtService} (HS256, validates iss + aud + exp), with a
 *       {@code JwtAuthenticationConverter} mapping the {@code role} claim to
 *       {@code ROLE_<role>} authorities.</li>
 *   <li>{@code .exceptionHandling()} wired to {@link RestAuthEntryPoint} (401) and
 *       {@link RestAccessDeniedHandler} (403), so errors use the ApiResponse envelope.</li>
 *   <li>{@code anyRequest().authenticated()} as the final rule.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())          // stateless JSON API, no cookies on this origin
                .cors(Customizer.withDefaults())        // uses CorsConfig (WebMvcConfigurer)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable())
                .logout(l -> l.disable())
                // TODO: replace with the rules listed in the class Javadoc.
                .authorizeHttpRequests(auth -> auth
                        // Service-to-service routes: guarded by the API-key interceptor
                        // (internal.InternalApiConfig), not user auth. Keep ahead of any anyRequest().authenticated().
                        .requestMatchers("/api/internal/**").permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }

    /**
     * BCrypt, matching the {@code $2a$/$2b$} hashes stored in auth.users.password_hash
     * (express-api's dev seed writes {@code $2b$} hashes, which this encoder verifies).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
