package app.service;

import app.dto.request.AuthRequest;
import app.dto.request.RegisterRequest;
import app.dto.response.LoginResponse;
import app.dto.response.Response;
import app.model.User;
import app.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public Response<User> register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return Response.error("نام کاربری نمی‌تواند خالی باشد");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Response.error("رمز عبور نمی‌تواند خالی باشد");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            return Response.error("نام کامل نمی‌تواند خالی باشد");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            return Response.error("شماره تماس نمی‌تواند خالی باشد");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return Response.error("این نام کاربری قبلاً ثبت شده است");
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(request.getPassword().trim());
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        user.setEmail(request.getEmail() != null ? request.getEmail().trim() : "");
        user.setRole(User.Role.USER);
        user.setStatus(User.UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        return Response.success("ثبت نام با موفقیت انجام شد", savedUser);
    }

    // ===== این متد اصلاح شده است =====
    public LoginResponse login(AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return LoginResponse.error("نام کاربری نمی‌تواند خالی باشد");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return LoginResponse.error("رمز عبور نمی‌تواند خالی باشد");
        }

        var userOpt = userRepository.findByUsername(request.getUsername().trim());
        if (userOpt.isEmpty()) {
            return LoginResponse.error("نام کاربری یا رمز عبور اشتباه است");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(request.getPassword().trim())) {
            return LoginResponse.error("نام کاربری یا رمز عبور اشتباه است");
        }

        if (user.getStatus() == User.UserStatus.BLOCKED) {
            return LoginResponse.error("حساب کاربری شما مسدود شده است");
        }

        return LoginResponse.success("ورود با موفقیت انجام شد", user);
    }

    public Response<User> getUserById(Long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Response.error("کاربر مورد نظر یافت نشد");
        }
        return Response.success("کاربر یافت شد", userOpt.get());
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}