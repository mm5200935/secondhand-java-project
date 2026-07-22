package app.controller;

import app.dto.response.RatingResponse;
import app.model.Advertisement;
import app.model.Rating;
import app.model.User;
import app.repository.interfaces.RatingRepository;
import app.security.AuthenticatedUser;
import app.service.interfaces.AdvertisementService;
import app.service.interfaces.RatingService;
import app.service.interfaces.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final RatingRepository ratingRepository;
    private final AdvertisementService advertisementService;
    private final UserService userService;

    public RatingController(RatingService ratingService,
                            RatingRepository ratingRepository,
                            AdvertisementService advertisementService,
                            UserService userService) {
        this.ratingService = ratingService;
        this.ratingRepository = ratingRepository;
        this.advertisementService = advertisementService;
        this.userService = userService;
    }

    // POST /api/ratings -> ثبت امتیاز برای فروشنده‌ی یک آگهی (نیاز به توکن)
    // بدنه: { "adId": 1, "score": 5, "comment": "..." }
    @PostMapping
    public RatingResponse rate(@RequestBody Map<String, Object> body,
                               @AuthenticationPrincipal AuthenticatedUser principal) {

        int adId = ((Number) body.get("adId")).intValue();
        int score = ((Number) body.get("score")).intValue();
        String comment = body.get("comment") != null ? body.get("comment").toString() : null;

        User buyer = userService.findById(principal.id());
        Advertisement ad = advertisementService.getById(adId);

        Rating rating = ratingService.addRating(buyer, ad, score, comment);
        return new RatingResponse(rating);
    }

    // GET /api/ratings/seller/{sellerId} -> همه‌ی امتیازهای یک فروشنده (عمومی)
    @GetMapping("/seller/{sellerId}")
    public List<RatingResponse> getSellerRatings(@PathVariable int sellerId) {
        User seller = userService.findById(sellerId);
        return ratingService.getSellerRatings(seller).stream()
                .map(RatingResponse::new)
                .collect(Collectors.toList());
    }

    // GET /api/ratings/seller/{sellerId}/average -> میانگین امتیاز یک فروشنده (عمومی)
    @GetMapping("/seller/{sellerId}/average")
    public double getSellerAverage(@PathVariable int sellerId) {
        User seller = userService.findById(sellerId);
        return ratingService.getAverageRating(seller);
    }

    // GET /api/ratings/has-rated/{adId} -> آیا کاربر فعلی قبلاً به این آگهی امتیاز داده؟
    @GetMapping("/has-rated/{adId}")
    public boolean hasRated(@PathVariable int adId,
                            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ratingRepository.existsByBuyerIdAndAdvertisementId(principal.id(), adId);
    }

    // GET /api/ratings/ad/{adId} -> همه‌ی نظرات ثبت‌شده روی یک آگهی خاص (عمومی)
    @GetMapping("/ad/{adId}")
    public List<RatingResponse> getAdRatings(@PathVariable int adId) {
        Advertisement ad = advertisementService.getById(adId);
        return ratingService.getAdRatings(ad).stream()
                .map(RatingResponse::new)
                .collect(Collectors.toList());
    }
}