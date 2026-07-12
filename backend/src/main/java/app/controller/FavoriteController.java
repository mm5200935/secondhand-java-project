package app.controller;

import app.model.Advertisement;
import app.model.Favorite;
import app.model.User;
import app.service.interfaces.AdvertisementService;
import app.service.interfaces.FavoriteService;
import app.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;
    private final AdvertisementService advertisementService;

    @Autowired
    public FavoriteController(
            FavoriteService favoriteService,
            UserService userService,
            AdvertisementService advertisementService
    ) {
        this.favoriteService = favoriteService;
        this.userService = userService;
        this.advertisementService = advertisementService;
    }

    @GetMapping
    public ResponseEntity<?> getUserFavorites(Principal principal) {
        try {
            User currentUser = userService.findByUsername(principal.getName());

            List<Favorite> favorites =
                    favoriteService.getUserFavorites(currentUser.getId());

            return ResponseEntity.ok(favorites);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/{adId}")
    public ResponseEntity<?> addFavorite(
            @PathVariable int adId,
            Principal principal
    ) {
        try {
            User currentUser = userService.findByUsername(principal.getName());

            Advertisement advertisement = advertisementService.getById(adId);

            if (advertisement == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Advertisement not found.");
            }

            favoriteService.addFavorite(currentUser, advertisement);

            return ResponseEntity.ok("آگهی با موفقیت به لیست علاقه‌مندی‌ها اضافه شد.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{adId}")
    public ResponseEntity<?> removeFavorite(
            @PathVariable int adId,
            Principal principal
    ) {
        try {
            User currentUser = userService.findByUsername(principal.getName());

            Advertisement advertisement = advertisementService.getById(adId);

            if (advertisement == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Advertisement not found.");
            }

            favoriteService.removeFavorite(currentUser, advertisement);

            return ResponseEntity.ok("آگهی از لیست علاقه‌مندی‌ها حذف شد.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countFavorites(Principal principal) {
        try {
            User currentUser = userService.findByUsername(principal.getName());

            int count = favoriteService.countUserFavorites(currentUser.getId());

            return ResponseEntity.ok(count);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/check/{adId}")
    public ResponseEntity<?> isFavorite(
            @PathVariable int adId,
            Principal principal
    ) {
        try {
            User currentUser = userService.findByUsername(principal.getName());

            Advertisement advertisement = advertisementService.getById(adId);

            if (advertisement == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Advertisement not found.");
            }

            boolean favorite = favoriteService.isFavorite(currentUser, advertisement);

            return ResponseEntity.ok(favorite);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}