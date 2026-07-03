package repository.impl;

import database.DatabaseManager;
import model.Advertisement;
import model.Conversation;
import model.User;
import repository.interfaces.ConversationRepository;
import repository.interfaces.UserRepository;
import repository.interfaces.AdvertisementRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationRepositoryImpl implements ConversationRepository {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public ConversationRepositoryImpl() {
        this.userRepository = new UserRepositoryImpl();
        this.advertisementRepository = new AdvertisementRepositoryImpl();
    }


    @Override
    public Conversation save(Conversation conversation) {

        String sql = """
                INSERT INTO conversations
                (buyer_id, seller_id, advertisement_id, closed)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setInt(1, conversation.getBuyer().getId());
            statement.setInt(2, conversation.getSeller().getId());
            statement.setInt(3, conversation.getAdvertisement().getId());
            statement.setInt(4, conversation.isClosed() ? 1 : 0);

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()) {
                conversation.setId(keys.getInt(1));
            }

            return conversation;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Conversation update(Conversation conversation) {

        String sql = """
                UPDATE conversations
                SET closed = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, conversation.isClosed() ? 1 : 0);
            statement.setInt(2, conversation.getId());

            statement.executeUpdate();

            return conversation;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(int conversationId) {

        String sql = "DELETE FROM conversations WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, conversationId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Conversation findById(int conversationId) {

        String sql = "SELECT * FROM conversations WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, conversationId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapConversation(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Conversation> findAll() {

        String sql = "SELECT * FROM conversations";

        List<Conversation> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {
                list.add(mapConversation(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Conversation> findByUserId(int userId) {

        String sql = """
                SELECT * FROM conversations
                WHERE buyer_id = ? OR seller_id = ?
                """;

        List<Conversation> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);
            statement.setInt(2, userId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapConversation(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Conversation findByUsersAndAdvertisement(int buyerId, int sellerId, int advertisementId) {

        String sql = """
                SELECT * FROM conversations
                WHERE buyer_id = ?
                  AND seller_id = ?
                  AND advertisement_id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, buyerId);
            statement.setInt(2, sellerId);
            statement.setInt(3, advertisementId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapConversation(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private Conversation mapConversation(ResultSet rs) throws SQLException {

        Conversation conversation = new Conversation();

        int buyerId = rs.getInt("buyer_id");
        int sellerId = rs.getInt("seller_id");
        int adId = rs.getInt("advertisement_id");

        User buyer = userRepository.findById(buyerId);
        User seller = userRepository.findById(sellerId);
        Advertisement ad = advertisementRepository.findById(adId);

        conversation.setId(rs.getInt("id"));
        conversation.setBuyer(buyer);
        conversation.setSeller(seller);
        conversation.setAdvertisement(ad);
        conversation.setClosed(rs.getInt("closed") == 1);

        return conversation;
    }
}