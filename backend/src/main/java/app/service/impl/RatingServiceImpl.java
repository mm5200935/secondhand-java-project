package app.service.impl;

import app.model.Rating;
import app.model.User;
import app.repository.interfaces.RatingRepository;
import app.service.interfaces.RatingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Rating submitRating(User buyer,
                               User seller,
                               int score,
                               String comment) {

        if (buyer == null) {
            throw new RuntimeException("Buyer not found.");
        }

        if (seller == null) {
            throw new RuntimeException("Seller not found.");
        }

        if (buyer.getId() == seller.getId()) {
            throw new RuntimeException("You cannot rate yourself.");
        }

        if (score < 1 || score > 5) {
            throw new RuntimeException("Score must be between 1 and 5.");
        }

        Rating rating = new Rating();
        rating.setBuyer(buyer);
        rating.setSeller(seller);
        rating.setScore(score);
        rating.setComment(comment);

        return ratingRepository.save(rating);
    }

    @Override
    public void updateRating(int ratingId,
                             User buyer,
                             int score,
                             String comment) {

        Rating rating = ratingRepository.findById(ratingId);

        if (rating == null) {
            throw new RuntimeException("Rating not found.");
        }

        if (rating.getBuyer().getId() != buyer.getId()) {
            throw new RuntimeException("Access denied.");
        }

        if (score < 1 || score > 5) {
            throw new RuntimeException("Score must be between 1 and 5.");
        }

        rating.setScore(score);
        rating.setComment(comment);

        ratingRepository.update(rating);
    }

    @Override
    public void deleteRating(int ratingId,
                             User buyer) {

        Rating rating = ratingRepository.findById(ratingId);

        if (rating == null) {
            throw new RuntimeException("Rating not found.");
        }

        if (rating.getBuyer().getId() != buyer.getId()) {
            throw new RuntimeException("Access denied.");
        }

        ratingRepository.delete(ratingId);
    }

    @Override
    public Rating getById(int ratingId) {
        return ratingRepository.findById(ratingId);
    }

    @Override
    public List<Rating> getSellerRatings(User seller) {
        return ratingRepository.findBySellerId(seller.getId());
    }

    @Override
    public double getAverageRating(User seller) {

        List<Rating> ratings = ratingRepository.findBySellerId(seller.getId());

        if (ratings == null || ratings.isEmpty()) {
            return 0;
        }

        double sum = 0;

        for (Rating rating : ratings) {
            sum += rating.getScore();
        }

        return sum / ratings.size();
    }
}