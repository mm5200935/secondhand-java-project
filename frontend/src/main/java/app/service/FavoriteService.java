package app.service;

import app.dto.response.Response;
import app.model.Ad;
import app.model.User;
import app.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class FavoriteService {
    private final UserRepository userRepository;
    private final AdService adService;

    public FavoriteService(UserRepository userRepository, AdService adService) {
        this.userRepository = userRepository;
        this.adService = adService;
    }

    public Response<Boolean> addFavorite(Long adId, Long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Response.error("کاربر یافت نشد");
        }
        User user = userOpt.get();

        if (user.getFavoriteAdIds().contains(adId)) {
            return Response.error("این آگهی قبلاً به علاقه‌مندی‌ها اضافه شده است");
        }

        // Check if ad exists and is active
        var adResponse = adService.getAdDetails(adId);
        if (!adResponse.isSuccess()) {
            return Response.error("آگهی مورد نظر یافت نشد");
        }

        user.getFavoriteAdIds().add(adId);
        userRepository.save(user);
        return Response.success("آگهی به علاقه‌مندی‌ها اضافه شد", true);
    }

    public Response<Boolean> removeFavorite(Long adId, Long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Response.error("کاربر یافت نشد");
        }
        User user = userOpt.get();

        if (!user.getFavoriteAdIds().contains(adId)) {
            return Response.error("این آگهی در علاقه‌مندی‌های شما وجود ندارد");
        }

        user.getFavoriteAdIds().remove(adId);
        userRepository.save(user);
        return Response.success("آگهی از علاقه‌مندی‌ها حذف شد", true);
    }

    public Response<List<Ad>> getFavorites(Long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Response.error("کاربر یافت نشد");
        }

        List<Ad> favoriteAds = new ArrayList<>();
        for (Long adId : userOpt.get().getFavoriteAdIds()) {
            var adResponse = adService.getAdDetails(adId);
            if (adResponse.isSuccess() && adResponse.getData() != null) {
                favoriteAds.add(adResponse.getData());
            }
        }

        return Response.success("لیست علاقه‌مندی‌ها", favoriteAds);
    }

    public Response<Boolean> isFavorite(Long adId, Long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Response.success("وضعیت علاقه‌مندی", false);
        }
        return Response.success("وضعیت علاقه‌مندی",
                userOpt.get().getFavoriteAdIds().contains(adId));
    }
}