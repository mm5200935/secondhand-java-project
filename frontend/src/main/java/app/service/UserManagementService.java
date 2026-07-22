package app.service;

import app.dto.response.Response;
import app.model.User;
import app.repository.UserRepository;

import java.util.List;

public class UserManagementService {
    private final UserRepository userRepository;

    public UserManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response<List<User>> getAllUsers(Long adminId) {
        var adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !adminOpt.get().isAdmin()) {
            return Response.error("فقط مدیر سیستم می‌تواند لیست کاربران را ببیند");
        }
        return Response.success("لیست کاربران", userRepository.findAll());
    }

    public Response<User> blockUser(Long userId, Long adminId) {
        var adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !adminOpt.get().isAdmin()) {
            return Response.error("فقط مدیر سیستم می‌تواند کاربر را مسدود کند");
        }

        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Response.error("کاربر یافت نشد");
        }

        User user = userOpt.get();
        if (user.isAdmin()) {
            return Response.error("نمی‌توان مدیر سیستم را مسدود کرد");
        }

        user.setStatus(User.UserStatus.BLOCKED);
        userRepository.save(user);
        return Response.success("کاربر با موفقیت مسدود شد", user);
    }

    public Response<User> unblockUser(Long userId, Long adminId) {
        var adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !adminOpt.get().isAdmin()) {
            return Response.error("فقط مدیر سیستم می‌تواند کاربر را فعال کند");
        }

        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Response.error("کاربر یافت نشد");
        }

        User user = userOpt.get();
        user.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(user);
        return Response.success("کاربر با موفقیت فعال شد", user);
    }
}