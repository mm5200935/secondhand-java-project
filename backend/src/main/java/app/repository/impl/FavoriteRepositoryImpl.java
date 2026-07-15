package app.repository.impl;
import app.database.DatabaseManager;
import app.model.Advertisement;
import app.model.Favorite;
import app.model.User;
import app.repository.interfaces.AdvertisementRepository;
import app.repository.interfaces.CategoryRepository;
import app.repository.interfaces.CityRepository;
import app.repository.interfaces.FavoriteRepository;
import app.repository.interfaces.UserRepository;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FavoriteRepositoryImpl implements FavoriteRepository {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;

    public FavoriteRepositoryImpl() {

        this.userRepository = new UserRepositoryImpl();
        this.categoryRepository = new CategoryRepositoryImpl();
        this.cityRepository = new CityRepositoryImpl();

        this.advertisementRepository =
                new AdvertisementRepositoryImpl(
                        userRepository,
                        categoryRepository,
                        cityRepository,
                        new AdvertisementImageRepositoryImpl()
                );

    }

    @Override
    public Favorite save(Favorite favorite) {

        if (existsByUserIdAndAdvertisementId(
                favorite.getUser().getId(),
                favorite.getAdvertisement().getId())) {
            return favorite;
        }

        String sql = """
                INSERT INTO favorites(user_id, advertisement_id)
                VALUES(?, ?)
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setInt(1, favorite.getUser().getId());
            statement.setInt(2, favorite.getAdvertisement().getId());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()) {
                favorite.setId(keys.getInt(1));
            }

            return favorite;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int favoriteId) {

        String sql = "DELETE FROM favorites WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, favoriteId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByUserIdAndAdvertisementId(int userId, int advertisementId) {

        String sql = """
                DELETE FROM favorites
                WHERE user_id = ? AND advertisement_id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);
            statement.setInt(2, advertisementId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Favorite findById(int favoriteId) {

        String sql = "SELECT * FROM favorites WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, favoriteId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapFavorite(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Favorite> findAll() {

        String sql = "SELECT * FROM favorites";

        List<Favorite> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {
                list.add(mapFavorite(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Favorite> findByUserId(int userId) {

        String sql = "SELECT * FROM favorites WHERE user_id = ?";

        List<Favorite> list = new ArrayList<>();

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapFavorite(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByUserIdAndAdvertisementId(int userId, int advertisementId) {

        String sql = """
                SELECT 1 FROM favorites
                WHERE user_id = ? AND advertisement_id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);
            statement.setInt(2, advertisementId);

            ResultSet rs = statement.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Favorite mapFavorite(ResultSet rs) throws SQLException {

        Favorite favorite = new Favorite();

        User user = userRepository.findById(rs.getInt("user_id"));
        Advertisement advertisement =
                advertisementRepository.findById(rs.getInt("advertisement_id"));

        favorite.setId(rs.getInt("id"));
        favorite.setUser(user);
        favorite.setAdvertisement(advertisement);

        return favorite;
    }
}