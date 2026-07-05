package app.repository.interfaces;

import app.model.Rating;

import java.util.List;

public interface RatingRepository {

    Rating save(Rating rating);

    Rating update(Rating rating);

    void delete(int ratingId);

    Rating findById(int ratingId);

    List<Rating> findAll();

    List<Rating> findBySellerId(int sellerId);

    List<Rating> findByBuyerId(int buyerId);

    List<Rating> findByAdvertisementId(int advertisementId);

    boolean existsByBuyerIdAndAdvertisementId(int buyerId, int advertisementId);

}