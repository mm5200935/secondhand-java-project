package app.controller;

import app.dto.request.AdvertisementRequest;
import app.dto.response.AdvertisementResponse;
import app.exception.UnauthorizedException;
import app.model.Advertisement;
import app.model.Category;
import app.model.City;
import app.model.User;
import app.security.AuthenticatedUser;
import app.service.interfaces.AdvertisementService;
import app.service.interfaces.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ads")
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final UserService userService;

    public AdvertisementController(AdvertisementService advertisementService, UserService userService) {
        this.advertisementService = advertisementService;
        this.userService = userService;
    }

    // GET /api/ads -> همه‌ی آگهی‌های فعال (برای صفحه‌ی اصلی)
    @GetMapping
    public List<AdvertisementResponse> getActiveAds() {
        return advertisementService.getAll().stream()
                .map(AdvertisementResponse::new)
                .collect(Collectors.toList());
    }

    // GET /api/ads/{id} -> جزئیات یک آگهی
    @GetMapping("/{id}")
    public AdvertisementResponse getById(@PathVariable int id) {
        return new AdvertisementResponse(advertisementService.getById(id));
    }

    // GET /api/ads/user/{userId} -> آگهی‌های یک کاربر خاص (برای "آگهی‌های من")
    @GetMapping("/user/{userId}")
    public List<AdvertisementResponse> getByUser(@PathVariable int userId) {
        return advertisementService.filterByUser(userId).stream()
                .map(AdvertisementResponse::new)
                .collect(Collectors.toList());
    }

    // POST /api/ads -> ساخت آگهی جدید (نیاز به توکن معتبر)
    @PostMapping
    public AdvertisementResponse create(@RequestBody AdvertisementRequest request,
                                        @AuthenticationPrincipal AuthenticatedUser principal) {

        User owner = userService.findById(principal.id());

        Advertisement ad = new Advertisement();
        ad.setTitle(request.getTitle());
        ad.setDescription(request.getDescription());
        ad.setPrice(request.getPrice());
        ad.setNegotiable(request.isNegotiable());
        ad.setCategory(new Category(request.getCategoryId(), null, null));
        ad.setCity(new City(request.getCityId(), null, null));

        return new AdvertisementResponse(
                advertisementService.create(ad, owner, request.getImagePaths())
        );
    }

    // PUT /api/ads/{id} -> ویرایش آگهی (فقط توسط مالک)
    @PutMapping("/{id}")
    public AdvertisementResponse update(@PathVariable int id,
                                        @RequestBody AdvertisementRequest request,
                                        @AuthenticationPrincipal AuthenticatedUser principal) {

        User owner = userService.findById(principal.id());

        Advertisement ad = new Advertisement();
        ad.setId(id);
        ad.setTitle(request.getTitle());
        ad.setDescription(request.getDescription());
        ad.setPrice(request.getPrice());
        ad.setNegotiable(request.isNegotiable());
        ad.setCategory(new Category(request.getCategoryId(), null, null));
        ad.setCity(new City(request.getCityId(), null, null));

        return new AdvertisementResponse(
                advertisementService.update(ad, owner, request.getImagePaths())
        );
    }

    // DELETE /api/ads/{id} -> حذف نرم آگهی (فقط توسط مالک)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id, @AuthenticationPrincipal AuthenticatedUser principal) {
        User owner = userService.findById(principal.id());
        advertisementService.delete(id, owner);
    }

    // POST /api/ads/{id}/sold -> علامت‌گذاری به‌عنوان فروخته‌شده (فقط توسط مالک)
    @PostMapping("/{id}/sold")
    public AdvertisementResponse markAsSold(@PathVariable int id,
                                            @AuthenticationPrincipal AuthenticatedUser principal) {

        Advertisement ad = advertisementService.getById(id);
        if (ad.getOwner() == null || ad.getOwner().getId() != principal.id()) {
            throw new UnauthorizedException("You are not the owner of this advertisement.");
        }

        advertisementService.markAsSold(id);
        return new AdvertisementResponse(advertisementService.getById(id));
    }

    // POST /api/ads/{id}/approve -> فقط ادمین
    @PostMapping("/{id}/approve")
    public AdvertisementResponse approve(@PathVariable int id,
                                         @AuthenticationPrincipal AuthenticatedUser principal) {
        requireAdmin(principal);
        advertisementService.approve(id);
        return new AdvertisementResponse(advertisementService.getById(id));
    }

    // POST /api/ads/{id}/reject -> فقط ادمین
    @PostMapping("/{id}/reject")
    public AdvertisementResponse reject(@PathVariable int id,
                                        @RequestBody(required = false) java.util.Map<String, String> body,
                                        @AuthenticationPrincipal AuthenticatedUser principal) {
        requireAdmin(principal);
        String reason = (body != null) ? body.get("reason") : null;
        advertisementService.reject(id, reason);
        return new AdvertisementResponse(advertisementService.getById(id));
    }

    // GET /api/ads/pending -> فقط ادمین، برای صفحه‌ی تایید آگهی‌ها
    @GetMapping("/pending")
    public List<AdvertisementResponse> getPending(@AuthenticationPrincipal AuthenticatedUser principal) {
        requireAdmin(principal);
        return advertisementService.getByStatus(app.enums.AdvertisementStatus.PENDING).stream()
                .map(AdvertisementResponse::new)
                .collect(Collectors.toList());
    }

    // GET /api/ads/search?keyword=... -> جست‌وجوی ساده
    // GET /api/ads/search?keyword=...&categoryId=...&cityId=...&minPrice=...&maxPrice=... -> جست‌وجوی پیشرفته
    @GetMapping("/search")
    public List<AdvertisementResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        return advertisementService.search(keyword, categoryId, cityId, minPrice, maxPrice).stream()
                .map(AdvertisementResponse::new)
                .collect(Collectors.toList());
    }

    // GET /api/ads/admin/all -> همه‌ی آگهی‌ها با هر وضعیتی (فقط ادمین)
    @GetMapping("/admin/all")
    public List<AdvertisementResponse> getAllForAdmin(@AuthenticationPrincipal AuthenticatedUser principal) {
        requireAdmin(principal);
        return advertisementService.getAllForAdmin().stream()
                .map(AdvertisementResponse::new)
                .collect(Collectors.toList());
    }

    // DELETE /api/ads/{id}/admin -> حذف هر آگهی توسط ادمین (بدون شرط مالکیت)
    @DeleteMapping("/{id}/admin")
    public void adminDelete(@PathVariable int id, @AuthenticationPrincipal AuthenticatedUser principal) {
        requireAdmin(principal);
        advertisementService.adminDelete(id);
    }

    // GET /api/ads/admin/search -> جست‌وجو روی همه آگهی‌ها با هر وضعیتی (فقط ادمین)
    @GetMapping("/admin/search")
    public List<AdvertisementResponse> searchForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @AuthenticationPrincipal AuthenticatedUser principal) {

        requireAdmin(principal);
        return advertisementService.searchForAdmin(keyword, categoryId, cityId, minPrice, maxPrice).stream()
                .map(AdvertisementResponse::new)
                .collect(Collectors.toList());
    }

    private void requireAdmin(AuthenticatedUser principal) {
        if (!"ADMIN".equalsIgnoreCase(principal.role())) {
            throw new UnauthorizedException("Admins only.");
        }
    }


}