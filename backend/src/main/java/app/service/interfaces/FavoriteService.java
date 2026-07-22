package app.service.interfaces;

import app.model.Advertisement;
import app.model.Favorite;
import app.model.User;

import java.util.List;

public interface FavoriteService {

    void addFavorite(User user, Advertisement advertisement);

    void removeFavorite(User user, Advertisement advertisement);

    boolean isFavorite(User user, Advertisement advertisement);

    List<Favorite> getUserFavorites(int userId);

    int countUserFavorites(int userId);

}