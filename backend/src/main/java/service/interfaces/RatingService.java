package service.interfaces;
import model.Advertisement;
import model.Rating;
import model.User;
import java.util.List;

public interface RatingService {

    Rating addRating(User buyer,
                     Advertisement advertisement,
                     int score,
                     String comment);

    void updateRating(int ratingId,
                      User buyer,
                      int score,
                      String comment);

    void deleteRating(int ratingId,
                      User buyer);

    Rating getById(int ratingId);

    List<Rating> getSellerRatings(User seller);

    double getAverageRating(User seller);

}