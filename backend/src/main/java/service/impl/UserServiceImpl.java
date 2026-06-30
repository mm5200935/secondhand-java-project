package service.impl;
import model.User;
import repository.interfaces.UserRepository;
import service.interfaces.UserService;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User register(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Wrong password");
        }

        if (user.isBlocked()) {
            throw new RuntimeException("User is blocked");
        }

        return user;
    }

    @Override
    public void logout(User user) {
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id);
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
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(newPassword);
        userRepository.update(user);
    }

    @Override
    public void deleteAccount(User user) {
        userRepository.delete(user.getId());
    }
}