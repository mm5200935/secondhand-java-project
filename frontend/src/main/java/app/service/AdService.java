package app.service;

import app.dto.request.AdRequest;
import app.dto.request.SearchRequest;
import app.dto.response.Response;
import app.model.Ad;
import app.repository.AdRepository;
import app.repository.CategoryRepository;
import app.repository.CityRepository;
import app.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AdService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;

    public AdService(AuthService authService, AdRepository adRepository,
                     CategoryRepository categoryRepository, CityRepository cityRepository) {
        this.adRepository = adRepository;
        this.userRepository = authService.getUserRepository();
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
    }

    public Response<Ad> createAd(AdRequest request, Long ownerId) {
        // Validation
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return Response.error("عنوان آگهی نمی‌تواند خالی باشد");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return Response.error("توضیحات آگهی نمی‌تواند خالی باشد");
        }
        if (request.getPrice() <= 0) {
            return Response.error("قیمت باید بزرگتر از صفر باشد");
        }

        // Check if user exists and is active
        var userOpt = userRepository.findById(ownerId);
        if (userOpt.isEmpty()) {
            return Response.error("کاربر یافت نشد");
        }
        if (userOpt.get().isBlocked()) {
            return Response.error("کاربر مسدود شده و نمی‌تواند آگهی ثبت کند");
        }

        // Check if category exists
        if (request.getCategoryId() != null) {
            if (categoryRepository.findById(request.getCategoryId()).isEmpty()) {
                return Response.error("دسته‌بندی انتخاب شده معتبر نیست");
            }
        }

        // Check if city exists
        if (request.getCityId() != null) {
            if (cityRepository.findById(request.getCityId()).isEmpty()) {
                return Response.error("شهر انتخاب شده معتبر نیست");
            }
        }

        // Create ad
        Ad ad = new Ad();
        ad.setTitle(request.getTitle().trim());
        ad.setDescription(request.getDescription().trim());
        ad.setPrice(request.getPrice());
        ad.setOwnerId(ownerId);
        ad.setCategoryId(request.getCategoryId());
        ad.setCityId(request.getCityId());
        ad.setImagePaths(request.getImagePaths() != null ? request.getImagePaths() : new ArrayList<>());
        ad.setStatus(Ad.AdStatus.PENDING);

        Ad savedAd = adRepository.save(ad);
        return Response.success("آگهی با موفقیت ثبت شد و در انتظار بررسی مدیر است", savedAd);
    }

    public Response<Ad> updateAd(Long adId, AdRequest request, Long userId) {
        var adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            return Response.error("آگهی یافت نشد");
        }

        Ad ad = adOpt.get();

        // Check ownership
        if (!ad.getOwnerId().equals(userId)) {
            return Response.error("شما اجازه ویرایش این آگهی را ندارید");
        }

        // Check if ad can be edited
        if (ad.getStatus() == Ad.AdStatus.SOLD || ad.getStatus() == Ad.AdStatus.DELETED) {
            return Response.error("این آگهی قابل ویرایش نیست");
        }

        // Update fields
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            ad.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            ad.setDescription(request.getDescription().trim());
        }
        if (request.getPrice() > 0) {
            ad.setPrice(request.getPrice());
        }
        if (request.getCategoryId() != null) {
            ad.setCategoryId(request.getCategoryId());
        }
        if (request.getCityId() != null) {
            ad.setCityId(request.getCityId());
        }
        if (request.getImagePaths() != null) {
            ad.setImagePaths(request.getImagePaths());
        }

        // Reset status to pending for re-review
        if (ad.getStatus() == Ad.AdStatus.ACTIVE || ad.getStatus() == Ad.AdStatus.REJECTED) {
            ad.setStatus(Ad.AdStatus.PENDING);
        }

        Ad updatedAd = adRepository.save(ad);
        return Response.success("آگهی با موفقیت ویرایش شد", updatedAd);
    }

    public Response<Ad> deleteAd(Long adId, Long userId) {
        var adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            return Response.error("آگهی یافت نشد");
        }

        Ad ad = adOpt.get();

        // Check ownership or admin
        if (!ad.getOwnerId().equals(userId)) {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty() || !userOpt.get().isAdmin()) {
                return Response.error("شما اجازه حذف این آگهی را ندارید");
            }
        }

        ad.setStatus(Ad.AdStatus.DELETED);
        adRepository.save(ad);
        return Response.success("آگهی با موفقیت حذف شد", null);
    }

    public Response<Ad> markAsSold(Long adId, Long userId) {
        var adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            return Response.error("آگهی یافت نشد");
        }

        Ad ad = adOpt.get();

        if (!ad.getOwnerId().equals(userId)) {
            return Response.error("شما اجازه تغییر وضعیت این آگهی را ندارید");
        }

        if (ad.getStatus() != Ad.AdStatus.ACTIVE) {
            return Response.error("فقط آگهی‌های فعال را می‌توان به فروخته شده تغییر داد");
        }

        ad.setStatus(Ad.AdStatus.SOLD);
        adRepository.save(ad);
        return Response.success("وضعیت آگهی به فروخته شده تغییر کرد", ad);
    }

    public Response<Ad> approveAd(Long adId, Long adminId) {
        var adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !adminOpt.get().isAdmin()) {
            return Response.error("فقط مدیر سیستم می‌تواند آگهی را تایید کند");
        }

        var adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            return Response.error("آگهی یافت نشد");
        }

        Ad ad = adOpt.get();
        if (ad.getStatus() != Ad.AdStatus.PENDING) {
            return Response.error("این آگهی در وضعیت در انتظار بررسی نیست");
        }

        ad.setStatus(Ad.AdStatus.ACTIVE);
        adRepository.save(ad);
        return Response.success("آگهی با موفقیت تایید شد", ad);
    }

    public Response<Ad> rejectAd(Long adId, String reason, Long adminId) {
        var adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !adminOpt.get().isAdmin()) {
            return Response.error("فقط مدیر سیستم می‌تواند آگهی را رد کند");
        }

        var adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            return Response.error("آگهی یافت نشد");
        }

        Ad ad = adOpt.get();
        if (ad.getStatus() != Ad.AdStatus.PENDING) {
            return Response.error("این آگهی در وضعیت در انتظار بررسی نیست");
        }

        ad.setStatus(Ad.AdStatus.REJECTED);
        ad.setRejectionReason(reason != null ? reason.trim() : "بدون توضیح");
        adRepository.save(ad);
        return Response.success("آگهی رد شد", ad);
    }

    public Response<List<Ad>> getAllActiveAds() {
        return Response.success("لیست آگهی‌های فعال", adRepository.findActiveAds());
    }

    public Response<List<Ad>> getPendingAds(Long adminId) {
        var adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !adminOpt.get().isAdmin()) {
            return Response.error("فقط مدیر سیستم می‌تواند آگهی‌های در انتظار را ببیند");
        }
        return Response.success("لیست آگهی‌های در انتظار بررسی", adRepository.findPendingAds());
    }

    public Response<Ad> getAdDetails(Long adId) {
        var adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            return Response.error("آگهی یافت نشد");
        }

        Ad ad = adOpt.get();
        if (!ad.isVisibleToPublic()) {
            // Check if requester is owner
            // For simplicity, we'll return error
            return Response.error("این آگهی قابل مشاهده نیست");
        }

        return Response.success("جزئیات آگهی", ad);
    }

    public Response<List<Ad>> getUserAds(Long userId) {
        return Response.success("لیست آگهی‌های کاربر", adRepository.findByOwnerId(userId));
    }

    public Response<List<Ad>> searchAds(SearchRequest request) {
        List<Ad> results = adRepository.findActiveAds();

        // Filter by keyword
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().trim().toLowerCase();
            results = results.stream()
                    .filter(a -> a.getTitle().toLowerCase().contains(keyword) ||
                            a.getDescription().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }

        // Filter by category
        if (request.getCategoryId() != null) {
            results = results.stream()
                    .filter(a -> request.getCategoryId().equals(a.getCategoryId()))
                    .collect(Collectors.toList());
        }

        // Filter by city
        if (request.getCityId() != null) {
            results = results.stream()
                    .filter(a -> request.getCityId().equals(a.getCityId()))
                    .collect(Collectors.toList());
        }

        // Filter by price range
        if (request.getMinPrice() != null) {
            results = results.stream()
                    .filter(a -> a.getPrice() >= request.getMinPrice())
                    .collect(Collectors.toList());
        }
        if (request.getMaxPrice() != null) {
            results = results.stream()
                    .filter(a -> a.getPrice() <= request.getMaxPrice())
                    .collect(Collectors.toList());
        }

        // Sort
        if (request.getSortBy() != null) {
            switch (request.getSortBy()) {
                case "price":
                    results.sort(Comparator.comparing(Ad::getPrice));
                    break;
                case "title":
                    results.sort(Comparator.comparing(Ad::getTitle));
                    break;
                case "createdAt":
                default:
                    results.sort(Comparator.comparing(Ad::getCreatedAt));
                    break;
            }
            if (!request.isAscending()) {
                Collections.reverse(results);
            }
        }

        return Response.success("نتایج جست‌وجو", results);
    }

    public AdRepository getAdRepository() {
        return adRepository;
    }
}