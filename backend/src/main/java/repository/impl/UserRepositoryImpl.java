package repository.impl;

import database.DatabaseManager;
import enums.UserRole;
import enums.UserStatus;
import model.User;
import repository.interfaces.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public User save(User user) {

        String sql = """
                INSERT INTO users
                (username, password, full_name, email, phone, role, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getRole().name());
            statement.setString(7, user.getStatus().name());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user.", e);
        }
    }

    @Override
    public User update(User user) {

        String sql = """
                UPDATE users
                SET username = ?,
                    password = ?,
                    full_name = ?,
                    email = ?,
                    phone = ?,
                    role = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getRole().name());
            statement.setString(7, user.getStatus().name());
            statement.setInt(8, user.getId());

            statement.executeUpdate();

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user.", e);
        }
    }

    @Override
    public void delete(int userId) {

        String sql = "DELETE FROM users WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user.", e);
        }
    }

    @Override
    public User findById(int userId) {

        String sql = "SELECT * FROM users WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapUser(resultSet);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id.", e);
        }
    }

    @Override
    public User findByUsername(String username) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapUser(resultSet);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username.", e);
        }
    }

    @Override
    public List<User> findAll() {

        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }

            return users;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding users.", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {

        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(int userId) {

        String sql = "SELECT 1 FROM users WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {

        User user = new User();

        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));
        user.setRole(UserRole.valueOf(resultSet.getString("role")));
        user.setStatus(UserStatus.valueOf(resultSet.getString("status")));

        return user;
    }
}