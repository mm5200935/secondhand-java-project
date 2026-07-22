package app.repository.impl;

import app.database.DatabaseManager;
import app.model.AdvertisementImage;
import app.repository.interfaces.AdvertisementImageRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AdvertisementImageRepositoryImpl implements AdvertisementImageRepository {

    @Override
    public void save(int advertisementId, String imagePath) {
        String sql = """
                INSERT INTO advertisement_images
                (image_path, advertisement_id, created_at, updated_at)
                VALUES (?,?,?,?)
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, imagePath);
            statement.setInt(2, advertisementId);
            statement.setString(3, LocalDateTime.now().toString());
            statement.setString(4, LocalDateTime.now().toString());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByAdvertisementId(int advertisementId) {
        String sql = "DELETE FROM advertisement_images WHERE advertisement_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, advertisementId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AdvertisementImage> findByAdvertisementId(int advertisementId) {
        String sql = "SELECT * FROM advertisement_images WHERE advertisement_id = ?";
        List<AdvertisementImage> images = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, advertisementId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                AdvertisementImage img = new AdvertisementImage(
                        rs.getInt("id"),
                        null,
                        rs.getString("image_path"),
                        0,
                        null
                );
                images.add(img);
            }
            return images;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}