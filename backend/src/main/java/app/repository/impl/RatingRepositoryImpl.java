package app.repository.impl;

import app.database.DatabaseManager;
import app.model.Advertisement;
import app.model.Rating;
import app.model.User;
import app.repository.interfaces.AdvertisementRepository;
import app.repository.interfaces.CategoryRepository;
import app.repository.interfaces.CityRepository;
import app.repository.interfaces.RatingRepository;
import app.repository.interfaces.UserRepository;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RatingRepositoryImpl implements RatingRepository {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;

    public RatingRepositoryImpl() {

        this.userRepository = new UserRepositoryImpl();
        this.categoryRepository = new CategoryRepositoryImpl();
        this.cityRepository = new CityRepositoryImpl();

        this.advertisementRepository = new AdvertisementRepositoryImpl(
                userRepository,
                categoryRepository,
                cityRepository);
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
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, rating.getBuyer().getId());
            statement.setInt(2, rating.getSeller().getId());

            if (rating.getAdvertisement() != null) {
                statement.setInt(3, rating.getAdvertisement().getId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

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
                PreparedStatement statement = connection.prepareStatement(sql)) {

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
                PreparedStatement statement = connection.prepareStatement(sql)) {

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
                PreparedStatement statement = connection.prepareStatement(sql)) {

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
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

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
                PreparedStatement statement = connection.prepareStatement(sql)) {

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
                PreparedStatement statement = connection.prepareStatement(sql)) {

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
                PreparedStatement statement = connection.prepareStatement(sql)) {

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
    public boolean existsByBuyerIdAndSellerId(int buyerId, int sellerId) {

        String sql = """
                SELECT 1
                FROM ratings
                WHERE buyer_id = ?
                  AND seller_id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, buyerId);
            statement.setInt(2, sellerId);

            ResultSet rs = statement.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Rating mapRating(ResultSet rs) throws SQLException {

        Rating rating = new Rating();

        User buyer = userRepository.findById(rs.getInt("buyer_id"));
        User seller = userRepository.findById(rs.getInt("seller_id"));
        Advertisement advertisement = null;

        int advertisementId = rs.getInt("advertisement_id");
        if (!rs.wasNull()) {
            advertisement = advertisementRepository.findById(advertisementId);
        }

        rating.setId(rs.getInt("id"));
        rating.setBuyer(buyer);
        rating.setSeller(seller);
        rating.setAdvertisement(advertisement);
        rating.setScore(rs.getInt("score"));
        rating.setComment(rs.getString("comment"));

        return rating;
    }
}