package app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for generating and validating JWT access tokens.
 * <p>
 * The token carries the user's id (as subject), username and role as claims,
 * so the rest of the backend can identify the currently authenticated user
 * without hitting the database on every request.
 */
@Component
public class JwtUtil {

    // NOTE: in a real production system this secret should come from an
    // environment variable / config file instead of being hard-coded.
    private static final String SECRET =
            "secondhand-marketplace-super-secret-key-change-me-1234567890";

    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24 hours

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(int userId, String username, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Parses and validates the token, returning its claims.
     * Throws JwtException (or a subclass) if the token is invalid or expired.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public int extractUserId(String token) {
        return Integer.parseInt(extractAllClaims(token).getSubject());
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
