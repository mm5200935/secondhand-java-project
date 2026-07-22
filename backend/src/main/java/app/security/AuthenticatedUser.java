
package app.security;

/**
 * Represents the identity of the currently authenticated user, extracted
 * from the JWT by {@link JwtAuthenticationFilter}. Controllers can obtain
 * this via {@code @AuthenticationPrincipal AuthenticatedUser user}.
 */
public record AuthenticatedUser(int id, String username, String role) {
}
