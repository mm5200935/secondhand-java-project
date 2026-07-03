package repository.impl;

import database.DatabaseManager;
import enums.AdvertisementStatus;
import model.Advertisement;
import model.Category;
import model.City;
import model.User;
import repository.interfaces.AdvertisementRepository;
import repository.interfaces.CategoryRepository;
import repository.interfaces.CityRepository;
import repository.interfaces.UserRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class AdvertisementRepositoryImpl implements AdvertisementRepository {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;

    public AdvertisementRepositoryImpl() {
        userRepository = new UserRepositoryImpl();
        categoryRepository = new CategoryRepositoryImpl();
        cityRepository = new CityRepositoryImpl();
    }

    @Override
    public Advertisement save(Advertisement advertisement) {

        String sql = """
                INSERT INTO advertisements
                (title,
                 description,
                 price,
                 negotiable,
                 status,
                 created_at,
                 updated_at,
                 seller_id,
                 category_id,
                 city_id)
                VALUES(?,?,?,?,?,?,?,?,?,?)
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(1, advertisement.getTitle());
            statement.setString(2, advertisement.getDescription());
            statement.setDouble(3, advertisement.getPrice());
            statement.setBoolean(4, advertisement.isNegotiable());
            statement.setString(5, advertisement.getStatus().name());

            statement.setString(
                    6,
                    advertisement.getCreatedAt().toString()
            );

            statement.setString(
                    7,
                    advertisement.getUpdatedAt().toString()
            );

            statement.setInt(
                    8,
                    advertisement.getOwner().getId()
            );

            statement.setInt(
                    9,
                    advertisement.getCategory().getId()
            );

            statement.setInt(
                    10,
                    advertisement.getCity().getId()
            );

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()) {
                advertisement.setId(keys.getInt(1));
            }

            return advertisement;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Advertisement update(Advertisement advertisement) {

        String sql = """
                UPDATE advertisements
                SET title = ?,
                    description = ?,
                    price = ?,
                    negotiable = ?,
                    status = ?,
                    updated_at = ?,
                    seller_id = ?,
                    category_id = ?,
                    city_id = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, advertisement.getTitle());
            statement.setString(2, advertisement.getDescription());
            statement.setDouble(3, advertisement.getPrice());
            statement.setBoolean(4, advertisement.isNegotiable());

            statement.setString(
                    5,
                    advertisement.getStatus().name()
            );

            statement.setString(
                    6,
                    LocalDateTime.now().toString()
            );

            statement.setInt(
                    7,
                    advertisement.getOwner().getId()
            );

            statement.setInt(
                    8,
                    advertisement.getCategory().getId()
            );

            statement.setInt(
                    9,
                    advertisement.getCity().getId()
            );

            statement.setInt(
                    10,
                    advertisement.getId()
            );

            statement.executeUpdate();

            return advertisement;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int advertisementId) {

        String sql = "DELETE FROM advertisements WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, advertisementId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Advertisement findById(int advertisementId) {

        String sql = "SELECT * FROM advertisements WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, advertisementId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapAdvertisement(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Advertisement> findAll() {

        String sql = "SELECT * FROM advertisements";

        List<Advertisement> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {
                list.add(mapAdvertisement(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Advertisement> findByUserId(int userId) {

        String sql = "SELECT * FROM advertisements WHERE seller_id = ?";

        List<Advertisement> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapAdvertisement(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Advertisement> findByCategoryId(int categoryId) {

        String sql = "SELECT * FROM advertisements WHERE category_id = ?";

        List<Advertisement> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, categoryId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapAdvertisement(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Advertisement> findByCityId(int cityId) {

        String sql = "SELECT * FROM advertisements WHERE city_id = ?";

        List<Advertisement> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, cityId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapAdvertisement(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Advertisement> findByStatus(enums.AdvertisementStatus status) {

        String sql = "SELECT * FROM advertisements WHERE status = ?";

        List<Advertisement> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, status.name());

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapAdvertisement(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Advertisement> search(String keyword) {

        String sql = """
                SELECT * FROM advertisements
                WHERE title LIKE ? OR description LIKE ?
                """;

        List<Advertisement> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            String search = "%" + keyword + "%";

            statement.setString(1, search);
            statement.setString(2, search);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapAdvertisement(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private Advertisement mapAdvertisement(ResultSet rs) throws SQLException {

        Advertisement ad = new Advertisement();

        ad.setId(rs.getInt("id"));
        ad.setTitle(rs.getString("title"));
        ad.setDescription(rs.getString("description"));
        ad.setPrice(rs.getDouble("price"));

        ad.setNegotiable(rs.getInt("negotiable") == 1);

        ad.setStatus(
                enums.AdvertisementStatus.valueOf(rs.getString("status"))
        );

        ad.setCreatedAt(
                java.time.LocalDateTime.parse(rs.getString("created_at"))
        );

        ad.setUpdatedAt(
                java.time.LocalDateTime.parse(rs.getString("updated_at"))
        );

        int sellerId = rs.getInt("seller_id");
        int categoryId = rs.getInt("category_id");
        int cityId = rs.getInt("city_id");

        User seller = new repository.impl.UserRepositoryImpl().findById(sellerId);
        Category category = new repository.impl.CategoryRepositoryImpl().findById(categoryId);
        City city = new repository.impl.CityRepositoryImpl().findById(cityId);

        ad.setOwner(seller);
        ad.setCategory(category);
        ad.setCity(city);

        return ad;
    }
}

