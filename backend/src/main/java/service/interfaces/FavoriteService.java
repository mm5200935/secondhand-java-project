package service.interfaces;

import model.Advertisement;
import model.Favorite;
import model.User;

import java.util.List;

public interface FavoriteService {

    void addFavorite(User user, Advertisement advertisement);

    void removeFavorite(User user, Advertisement advertisement);

    boolean isFavorite(User user, Advertisement advertisement);

    List<Favorite> getUserFavorites(int userId);

    int countUserFavorites(int userId);

}