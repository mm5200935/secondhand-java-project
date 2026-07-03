package model;
import enums.UserRole;
import enums.UserStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {

    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private double averageRating;
    private List<Advertisement> advertisements;
    private List<Favorite> favorites;
    private List<Conversation> conversations;


    public User() {
        advertisements = new ArrayList<>();
        favorites = new ArrayList<>();
        conversations = new ArrayList<>();
        createdAt = LocalDateTime.now();
        status = UserStatus.ACTIVE;
        averageRating = 0;
    }

    public User(int id,
                String username,
                String password,
                String firstName,
                String lastName,
                String email,
                String phoneNumber,
                UserRole role) {

        this();

        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }

    public boolean isBlocked() {
        return status == UserStatus.BLOCKED;
    }

    public int getId() {
        return this.id;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phoneNumber;
    }

    public UserRole getRole() {
        return this.role;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {

        String[] parts = fullName.trim().split("\\s+", 2);

        this.firstName = parts[0];

        if (parts.length > 1) {
            this.lastName = parts[1];
        } else {
            this.lastName = "";
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }


}