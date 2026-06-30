package service.impl;
import model.Advertisement;
import model.Favorite;
import model.User;
import repository.interfaces.FavoriteRepository;
import service.interfaces.FavoriteService;

import java.util.List;

public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public void addFavorite(User user, Advertisement advertisement) {

        if (favoriteRepository.existsByUserIdAndAdvertisementId(
                user.getId(), advertisement.getId())) {

            throw new RuntimeException("Advertisement is already in favorites.");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setAdvertisement(advertisement);

        favoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(User user, Advertisement advertisement) {

        if (!favoriteRepository.existsByUserIdAndAdvertisementId(
                user.getId(), advertisement.getId())) {

            throw new RuntimeException("Favorite not found.");
        }

        favoriteRepository.deleteByUserIdAndAdvertisementId(
                user.getId(), advertisement.getId());
    }

    @Override
    public boolean isFavorite(User user, Advertisement advertisement) {

        return favoriteRepository.existsByUserIdAndAdvertisementId(
                user.getId(), advertisement.getId());
    }

    @Override
    public List<Favorite> getUserFavorites(int userId) {

        return favoriteRepository.findByUserId(userId);
    }

    @Override
    public int countUserFavorites(int userId) {

        return favoriteRepository.findByUserId(userId).size();
    }
}