package app.service.impl;

import app.model.User;
import app.exception.ResourceNotFoundException;
import app.repository.interfaces.UserRepository;
import app.service.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        if (!user.getPassword().equals(password)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wrong password"
            );
        }

        if (user.isBlocked()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User is blocked"
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

        if (!user.getPassword().equals(oldPassword)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Old password is incorrect"
            );
        }

        user.setPassword(newPassword);
        userRepository.update(user);
    }

    @Override
    public void deleteAccount(User user) {
        userRepository.delete(user.getId());
    }
}