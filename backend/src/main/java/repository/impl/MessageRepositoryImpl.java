package repository.impl;

import database.DatabaseManager;
import model.Message;
import model.User;
import repository.interfaces.MessageRepository;
import repository.interfaces.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepositoryImpl implements MessageRepository {

    private final UserRepository userRepository;

    public MessageRepositoryImpl() {
        this.userRepository = new UserRepositoryImpl();
    }


    @Override
    public Message save(Message message) {

        String sql = """
                INSERT INTO messages
                (conversation_id, sender_id, content, sent_at)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setInt(1, message.getConversationId());
            statement.setInt(2, message.getSender().getId());
            statement.setString(3, message.getContent());

            if (message.getSentAt() != null) {
                statement.setString(4, message.getSentAt().toString());
            } else {
                statement.setString(4, java.time.LocalDateTime.now().toString());
            }

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()) {
                message.setId(keys.getInt(1));
            }

            return message;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Message update(Message message) {

        String sql = """
                UPDATE messages
                SET content = ?,
                    seen = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, message.getContent());
            statement.setBoolean(2, message.isSeen());
            statement.setInt(3, message.getId());

            statement.executeUpdate();

            return message;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(int messageId) {

        String sql = "DELETE FROM messages WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, messageId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Message findById(int messageId) {

        String sql = "SELECT * FROM messages WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, messageId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapMessage(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Message> findAll() {

        String sql = "SELECT * FROM messages";

        List<Message> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {
                list.add(mapMessage(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Message> findByConversationId(int conversationId) {

        String sql = """
                SELECT * FROM messages
                WHERE conversation_id = ?
                ORDER BY id ASC
                """;

        List<Message> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, conversationId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapMessage(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Message> findBySenderId(int senderId) {

        String sql = """
                SELECT * FROM messages
                WHERE sender_id = ?
                ORDER BY id DESC
                """;

        List<Message> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, senderId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapMessage(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private Message mapMessage(ResultSet rs) throws SQLException {

        Message message = new Message();

        int senderId = rs.getInt("sender_id");

        User sender = userRepository.findById(senderId);

        message.setId(rs.getInt("id"));
        message.setConversationId(rs.getInt("conversation_id"));
        message.setSender(sender);
        message.setContent(rs.getString("content"));
        message.setSeen(rs.getBoolean("seen"));

        String sentAt = rs.getString("sent_at");
        if (sentAt != null) {
            message.setSentAt(java.time.LocalDateTime.parse(sentAt));
        }

        return message;
    }
}