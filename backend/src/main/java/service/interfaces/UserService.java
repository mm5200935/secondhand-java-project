package app.service.interfaces;
import app.model.User;
import java.util.List;

public interface UserService {

    User register(User user);
    User login(String username, String password);
    void logout(User user);
    User findById(int id);
    User findByUsername(String username);
    List<User> getAllUsers();
    void updateProfile(User user);
    void changePassword(User user,
                        String oldPassword,
                        String newPassword);
    void deleteAccount(User user);
}