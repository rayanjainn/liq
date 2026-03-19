package com.collabnex.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration that enforces JWT-based stateless authentication
 * and role-based route-level access control.
 *
 * <p>Route rules:
 * <ul>
 *   <li>{@code /auth/**} — public (register, login)</li>
 *   <li>{@code /uploads/**} — public (serve static uploaded files)</li>
 *   <li>{@code /freelancer/**} — requires ROLE_FREELANCER</li>
 *   <li>{@code /client/**} — requires ROLE_CLIENT</li>
 *   <li>{@code /admin/**} — requires ROLE_ADMIN</li>
 *   <li>All other routes — require authentication</li>
 * </ul>
 * </p>
 *
 * <p>Method-level security is also enabled via {@link EnableMethodSecurity}
 * to allow {@code @PreAuthorize} annotations on individual controller methods.</p>
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Configures the security filter chain with CSRF disabled (stateless API),
     * CORS enabled, stateless session management, route-level authorization rules,
     * and the custom JWT filter inserted before Spring's UsernamePasswordAuthenticationFilter.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the built {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/freelancer/**").hasRole("FREELANCER")
                .requestMatchers("/client/**").hasRole("CLIENT")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
