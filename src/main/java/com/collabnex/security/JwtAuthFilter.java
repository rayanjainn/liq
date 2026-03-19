package com.collabnex.security;

import com.collabnex.config.CustomUserDetails;
import com.collabnex.domain.user.User;
import com.collabnex.domain.user.UserRole;
import com.collabnex.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter that intercepts every HTTP request and, if a valid
 * {@code Authorization: Bearer <token>} header is present, authenticates the user
 * into the Spring Security context.
 *
 * <p>For admin tokens (where {@code uid == -1}), a virtual {@link CustomUserDetails}
 * is constructed without a database lookup. For regular users, the user is loaded
 * from the database via {@link UserService}.</p>
 *
 * <p>If the token is missing or invalid, the request continues unauthenticated
 * (Spring Security's authorization rules will then decide whether to allow or reject it).</p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Value("${app.admin.email:admin@collabnex.com}")
    private String adminEmail;

    /**
     * Constructs the filter with required dependencies.
     *
     * @param jwtService  service for parsing/validating JWT tokens
     * @param userService service for loading user details from the database
     */
    public JwtAuthFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * Extracts and validates the JWT from the Authorization header.
     * On success, places a fully-authenticated {@link UsernamePasswordAuthenticationToken}
     * into the {@link SecurityContextHolder} so downstream filters and controllers
     * can access the authenticated principal.
     *
     * @param request  the incoming HTTP request
     * @param response the HTTP response
     * @param chain    the filter chain to continue processing
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.parse(token).getBody();
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                Long uid = claims.get("uid", Long.class);

                UserDetails userDetails;

                if (uid != null && uid == -1L && "ADMIN".equals(role)) {
                    // Virtual admin user — not in the database
                    userDetails = new CustomUserDetails(-1L, adminEmail, "Admin", "", UserRole.ADMIN);
                } else {
                    // Regular user — load from DB
                    User user = userService.getByEmail(email);
                    userDetails = new CustomUserDetails(user);
                }

                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) {
                // Invalid token — request continues unauthenticated
            }
        }
        chain.doFilter(request, response);
    }
}
