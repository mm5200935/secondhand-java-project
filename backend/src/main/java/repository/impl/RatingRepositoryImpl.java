package repository.impl;

import database.DatabaseManager;
import model.Advertisement;
import model.Rating;
import model.User;
import repository.interfaces.AdvertisementRepository;
import repository.interfaces.RatingRepository;
import repository.interfaces.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingRepositoryImpl implements RatingRepository {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public RatingRepositoryImpl() {
        this.userRepository = new UserRepositoryImpl();
        this.advertisementRepository = new AdvertisementRepositoryImpl();
    }


    @Override
    public Rating save(Rating rating) {

        String sql = """
                INSERT INTO ratings
                (buyer_id, seller_id, advertisement_id, score, comment)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setInt(1, rating.getBuyer().getId());
            statement.setInt(2, rating.getSeller().getId());
            statement.setInt(3, rating.getAdvertisement().getId());
            statement.setInt(4, rating.getScore());
            statement.setString(5, rating.getComment());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()) {
                rating.setId(keys.getInt(1));
            }

            return rating;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Rating update(Rating rating) {

        String sql = """
                UPDATE ratings
                SET score = ?,
                    comment = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, rating.getScore());
            statement.setString(2, rating.getComment());
            statement.setInt(3, rating.getId());

            statement.executeUpdate();

            return rating;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(int ratingId) {

        String sql = "DELETE FROM ratings WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, ratingId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Rating findById(int ratingId) {

        String sql = "SELECT * FROM ratings WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, ratingId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRating(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Rating> findAll() {

        String sql = "SELECT * FROM ratings";

        List<Rating> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {
                list.add(mapRating(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Rating> findBySellerId(int sellerId) {

        String sql = "SELECT * FROM ratings WHERE seller_id = ?";

        List<Rating> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, sellerId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapRating(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Rating> findByBuyerId(int buyerId) {

        String sql = "SELECT * FROM ratings WHERE buyer_id = ?";

        List<Rating> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, buyerId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapRating(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Rating> findByAdvertisementId(int advertisementId) {

        String sql = "SELECT * FROM ratings WHERE advertisement_id = ?";

        List<Rating> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, advertisementId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapRating(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean existsByBuyerIdAndAdvertisementId(int buyerId, int advertisementId) {

        String sql = """
                SELECT 1 FROM ratings
                WHERE buyer_id = ? AND advertisement_id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, buyerId);
            statement.setInt(2, advertisementId);

            ResultSet rs = statement.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private Rating mapRating(ResultSet rs) throws SQLException {

        Rating rating = new Rating();

        int buyerId = rs.getInt("buyer_id");
        int sellerId = rs.getInt("seller_id");
        int adId = rs.getInt("advertisement_id");

        User buyer = userRepository.findById(buyerId);
        User seller = userRepository.findById(sellerId);
        Advertisement ad = advertisementRepository.findById(adId);

        rating.setId(rs.getInt("id"));
        rating.setBuyer(buyer);
        rating.setSeller(seller);
        rating.setAdvertisement(ad);
        rating.setScore(rs.getInt("score"));
        rating.setComment(rs.getString("comment"));

        return rating;
    }
}