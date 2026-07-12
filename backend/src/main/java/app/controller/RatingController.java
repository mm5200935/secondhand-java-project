package app.controller;

import app.dto.RatingRequest;
import app.model.User;
import app.service.interfaces.RatingService;
import app.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;

    @Autowired
    public RatingController(RatingService ratingService,
                            UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> submitRating(@RequestBody RatingRequest request,
                                          Principal principal) {

        try {

            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User is not authenticated.");
            }

            if (request.getScore() < 1 || request.getScore() > 5) {
                return ResponseEntity.badRequest()
                        .body("Score must be between 1 and 5.");
            }

            User buyer = userService.findByUsername(principal.getName());

            if (buyer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Buyer not found.");
            }

            User seller = userService.findById(request.getSellerId());

            if (seller == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Seller not found.");
            }

            ratingService.submitRating(
                    buyer,
                    seller,
                    request.getScore(),
                    request.getComment()
            );

            return ResponseEntity.ok("Rating submitted successfully.");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}