package app.service;

import app.dto.request.RatingRequest;
import app.dto.response.Response;
import app.model.Ad;
import app.model.Rating;
import app.model.User;
import app.repository.RatingRepository;
import app.repository.UserRepository;

import java.util.List;

public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final AdService adService;

    public RatingService(RatingRepository ratingRepository, UserRepository userRepository, AdService adService) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.adService = adService;
    }

    public Response<Rating> rateSeller(RatingRequest request) {
        // Validation
        if (request.getAdId() == null) {
            return Response.error("شناسه آگهی الزامی است");
        }
        if (request.getRaterId() == null) {
            return Response.error("شناسه امتیازدهنده الزامی است");
        }
        if (request.getScore() < 1 || request.getScore() > 5) {
            return Response.error("امتیاز باید بین 1 تا 5 باشد");
        }

        // Check rater
        var raterOpt = userRepository.findById(request.getRaterId());
        if (raterOpt.isEmpty()) {
            return Response.error("کاربر یافت نشد");
        }
        User rater = raterOpt.get();
        if (rater.isBlocked()) {
            return Response.error("کاربر مسدود شده است");
        }

        // Get ad
        var adResponse = adService.getAdDetails(request.getAdId());
        if (!adResponse.isSuccess() || adResponse.getData() == null) {
            return Response.error("آگهی مورد نظر یافت نشد");
        }
        Ad ad = adResponse.getData();

        // Check if rater is the seller
        if (ad.getOwnerId().equals(request.getRaterId())) {
            return Response.error("شما نمی‌توانید به خودتان امتیاز دهید");
        }

        // Check if already rated
        var existingRating = ratingRepository.findByAdIdAndRaterId(request.getAdId(), request.getRaterId());
        if (existingRating.isPresent()) {
            return Response.error("شما قبلاً به این فروشنده برای این آگهی امتیاز داده‌اید");
        }

        // Create rating
        Rating rating = new Rating();
        rating.setAdId(request.getAdId());
        rating.setRaterId(request.getRaterId());
        rating.setSellerId(ad.getOwnerId());
        rating.setScore(request.getScore());
        rating.setComment(request.getComment() != null ? request.getComment().trim() : "");

        Rating savedRating = ratingRepository.save(rating);

        // Update seller rating
        updateSellerRating(ad.getOwnerId());

        return Response.success("امتیاز با موفقیت ثبت شد", savedRating);
    }

    private void updateSellerRating(Long sellerId) {
        List<Rating> ratings = ratingRepository.findBySellerId(sellerId);
        if (ratings.isEmpty()) return;

        double sum = ratings.stream().mapToInt(Rating::getScore).sum();
        double average = sum / ratings.size();

        userRepository.updateRating(sellerId, average, ratings.size());
    }

    public Response<List<Rating>> getSellerRatings(Long sellerId) {
        var userOpt = userRepository.findById(sellerId);
        if (userOpt.isEmpty()) {
            return Response.error("فروشنده یافت نشد");
        }
        return Response.success("لیست امتیازها", ratingRepository.findBySellerId(sellerId));
    }

    public Response<Double> getSellerAverageRating(Long sellerId) {
        var userOpt = userRepository.findById(sellerId);
        if (userOpt.isEmpty()) {
            return Response.error("فروشنده یافت نشد");
        }
        return Response.success("میانگین امتیاز", userOpt.get().getAverageRating());
    }

    public Response<Boolean> hasUserRatedAd(Long adId, Long userId) {
        return Response.success("وضعیت امتیاز", ratingRepository.findByAdIdAndRaterId(adId, userId).isPresent());
    }
}