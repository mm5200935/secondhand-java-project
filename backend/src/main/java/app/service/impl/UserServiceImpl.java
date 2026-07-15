package app.service.impl;

import app.enums.UserRole;
import app.model.User;
import app.exception.ResourceNotFoundException;
import app.repository.interfaces.UserRepository;
import app.service.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "نام کاربری قبلاً وجود دارد"
            );
        }

        if (userRepository.existsByPhone(user.getPhone())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "این شماره تماس قبلاً در سیستم ثبت شده است"
            );
        }

        // default role for every new sign-up is a regular user
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }

        // never store the raw password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "نام کاربری یافت نشد"
            );
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "رمز عبور اشتباه است"
            );
        }

        if (user.isBlocked()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "حساب کاربری شما مسدود شده است"
            );
        }

        return user;
    }

    @Override
    public void logout(User user) {
        //oprtional
    }

    @Override
    public User findById(int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found.");
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateProfile(User user) {
        userRepository.update(user);
    }

    @Override
    public void changePassword(User user,
                               String oldPassword,
                               String newPassword) {

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Old password is incorrect"
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.update(user);
    }

    @Override
    public void deleteAccount(User user) {
        userRepository.delete(user.getId());
    }

    @Override
    public User blockUser(int userId) {
        User user = findById(userId);
        user.setStatus(app.enums.UserStatus.BLOCKED);
        return userRepository.update(user);
    }

    @Override
    public User unblockUser(int userId) {
        User user = findById(userId);
        user.setStatus(app.enums.UserStatus.ACTIVE);
        return userRepository.update(user);
    }
}