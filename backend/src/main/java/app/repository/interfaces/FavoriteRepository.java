package app.repository.interfaces;

import app.model.Favorite;

import java.util.List;

public interface FavoriteRepository {

    Favorite save(Favorite favorite);

    void delete(int favoriteId);

    void deleteByUserIdAndAdvertisementId(int userId, int advertisementId);

    Favorite findById(int favoriteId);

    List<Favorite> findAll();

    List<Favorite> findByUserId(int userId);

    boolean existsByUserIdAndAdvertisementId(int userId, int advertisementId);

}