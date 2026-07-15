package app.dto.response;

import app.model.User;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private User user;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public static LoginResponse success(String message, User user) {
        return new LoginResponse(true, message, user);
    }

    public static LoginResponse error(String message) {
        return new LoginResponse(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}