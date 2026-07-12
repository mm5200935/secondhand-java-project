package app.dto;

import app.enums.UserRole;

public class AuthResponse {
    private String token;
    private int userId;
    private String username;
    private UserRole role;

    public AuthResponse(String token, int userId, String username, UserRole role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // Getters
    public String getToken() { return token; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public UserRole getRole() { return role; }
}