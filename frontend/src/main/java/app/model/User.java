package app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Role role;
    private UserStatus status;
    private double averageRating;
    private int ratingCount;
    private List<Long> favoriteAdIds;
    private List<Long> conversationIds;

    public enum Role {
        USER, ADMIN
    }

    public enum UserStatus {
        ACTIVE, BLOCKED
    }

    public User() {
        this.favoriteAdIds = new ArrayList<>();
        this.conversationIds = new ArrayList<>();
        this.status = UserStatus.ACTIVE;
        this.role = Role.USER;
        this.averageRating = 0.0;
        this.ratingCount = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public List<Long> getFavoriteAdIds() { return favoriteAdIds; }
    public void setFavoriteAdIds(List<Long> favoriteAdIds) { this.favoriteAdIds = favoriteAdIds; }

    public List<Long> getConversationIds() { return conversationIds; }
    public void setConversationIds(List<Long> conversationIds) { this.conversationIds = conversationIds; }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isBlocked() {
        return status == UserStatus.BLOCKED;
    }
}