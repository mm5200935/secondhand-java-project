package app.repository.impl;
import app.database.DatabaseManager;
import app.model.City;
import app.repository.interfaces.CityRepository;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CityRepositoryImpl implements CityRepository {

    @Override
    public City save(City city) {

        String sql = """
                INSERT INTO cities(name, province)
                VALUES(?,?)
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(1, city.getName());
            statement.setString(2, city.getProvince());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                city.setId(generatedKeys.getInt(1));
            }

            return city;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public City update(City city) {

        String sql = """
                UPDATE cities
                SET name = ?,
                    province = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, city.getName());
            statement.setString(2, city.getProvince());
            statement.setInt(3, city.getId());

            statement.executeUpdate();

            return city;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int cityId) {

        String sql = "DELETE FROM cities WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, cityId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public City findById(int cityId) {

        String sql = "SELECT * FROM cities WHERE id = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, cityId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapCity(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<City> findAll() {

        List<City> cities = new ArrayList<>();

        String sql = "SELECT * FROM cities";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {
                cities.add(mapCity(rs));
            }

            return cities;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public City findByName(String name) {

        String sql = "SELECT * FROM cities WHERE name = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapCity(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByName(String name) {

        String sql = "SELECT 1 FROM cities WHERE name = ?";

        try (
                Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, name);

            ResultSet rs = statement.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private City mapCity(ResultSet rs) throws SQLException {

        City city = new City();

        city.setId(rs.getInt("id"));
        city.setName(rs.getString("name"));
        city.setProvince(rs.getString("province"));

        return city;
    }
}