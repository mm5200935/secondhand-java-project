package app.controller;

import app.dto.response.AdvertisementResponse;
import app.model.Advertisement;
import app.model.User;
import app.security.AuthenticatedUser;
import app.service.interfaces.AdvertisementService;
import app.service.interfaces.FavoriteService;
import app.service.interfaces.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AdvertisementService advertisementService;
    private final UserService userService;

    public FavoriteController(FavoriteService favoriteService,
                              AdvertisementService advertisementService,
                              UserService userService) {
        this.favoriteService = favoriteService;
        this.advertisementService = advertisementService;
        this.userService = userService;
    }

    @PostMapping("/{adId}")
    public boolean addFavorite(@PathVariable int adId,
                               @AuthenticationPrincipal AuthenticatedUser principal) {
        User user = userService.findById(principal.id());
        Advertisement ad = advertisementService.getById(adId);
        favoriteService.addFavorite(user, ad);
        return true;
    }

    @DeleteMapping("/{adId}")
    public boolean removeFavorite(@PathVariable int adId,
                                  @AuthenticationPrincipal AuthenticatedUser principal) {
        User user = userService.findById(principal.id());
        Advertisement ad = advertisementService.getById(adId);
        favoriteService.removeFavorite(user, ad);
        return true;
    }

    @GetMapping("/check/{adId}")
    public boolean isFavorite(@PathVariable int adId,
                              @AuthenticationPrincipal AuthenticatedUser principal) {
        User user = userService.findById(principal.id());
        Advertisement ad = advertisementService.getById(adId);
        return favoriteService.isFavorite(user, ad);
    }

    @GetMapping("/user/{userId}")
    public List<AdvertisementResponse> getUserFavorites(@PathVariable int userId) {
        return favoriteService.getUserFavorites(userId).stream()
                .map(fav -> new AdvertisementResponse(fav.getAdvertisement()))
                .collect(Collectors.toList());
    }
}