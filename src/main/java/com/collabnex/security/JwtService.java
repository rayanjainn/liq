package com.collabnex.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * Service responsible for generating and parsing JSON Web Tokens (JWTs).
 * The signing key, expiration duration are sourced from application properties,
 * making them configurable per environment.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    /**
     * Constructs the JwtService with the configured secret and expiration time.
     *
     * @param secret       the HMAC-SHA256 secret string (from {@code app.jwt.secret})
     * @param expirationMs token lifetime in milliseconds (from {@code app.jwt.expiration-ms})
     */
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT with the given subject (email) and additional claims.
     * Standard claims: {@code sub} (email), {@code iat} (issued at), {@code exp} (expiry).
     * Custom claims typically include {@code role} and {@code uid}.
     *
     * @param subject the token subject — usually the user's email address
     * @param claims  additional claims to embed (e.g., role, uid)
     * @return a compact, signed JWT string
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses and validates a JWT string. Throws a {@link JwtException} if the
     * token is expired, malformed, or has an invalid signature.
     *
     * @param token the compact JWT string
     * @return the parsed {@link Jws} containing the token's claims
     * @throws JwtException if the token cannot be validated
     */
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
