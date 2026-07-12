package app.controller;

import app.dto.AdvertisementRequest;
import app.model.Advertisement;
import app.model.Category;
import app.model.City;
import app.model.User;
import app.service.interfaces.AdvertisementService;
import app.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.security.Principal;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final UserService userService; // برای پیدا کردن کاربری که آگهی را ثبت می‌کند

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService, UserService userService) {
        this.advertisementService = advertisementService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Advertisement>> getAllAds() {
        List<Advertisement> ads = advertisementService.getAll();
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdById(@PathVariable int id) {
        try {
            Advertisement ad = advertisementService.getById(id);
            return ResponseEntity.ok(ad);
        } catch (Exception e) {
            return new ResponseEntity<>("Advertisement not found", HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping
    public ResponseEntity<?> createAd(@RequestBody AdvertisementRequest request, Principal principal) {
        try {
            // نام کاربری به صورت امن از توکن استخراج شده و به این متد پاس داده می‌شود
            String username = principal.getName(); 
            User owner = userService.findByUsername(username); 

            Advertisement ad = new Advertisement();
            ad.setTitle(request.getTitle());
            ad.setDescription(request.getDescription());
            ad.setPrice(request.getPrice());
            ad.setNegotiable(request.isNegotiable());

            Category category = new Category();
            category.setId(request.getCategoryId());
            ad.setCategory(category);

            City city = new City();
            city.setId(request.getCityId());
            ad.setCity(city);

            Advertisement createdAd = advertisementService.create(ad, owner);
            return new ResponseEntity<>(createdAd, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Advertisement>> searchAdvertisements(@RequestParam String keyword) {
        List<Advertisement> result = advertisementService.search(keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Advertisement>> filterByCategory(@PathVariable int categoryId) {
        List<Advertisement> result = advertisementService.filterByCategory(categoryId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<Advertisement>> filterByCity(@PathVariable int cityId) {
        List<Advertisement> result = advertisementService.filterByCity(cityId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/sold")
    public ResponseEntity<?> markAsSold(@PathVariable int id, Principal principal) {
        try {
            // آپشنال: می‌توانید قبل از فروخته شده علامت زدن، بررسی کنید که آیا کاربر جاری صاحب آگهی است یا خیر
            advertisementService.markAsSold(id);
            return ResponseEntity.ok("وضعیت آگهی به 'فروخته شده' تغییر یافت.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateAd(@PathVariable int id) {
        try {
            advertisementService.deactivate(id);
            return ResponseEntity.ok("آگهی با موفقیت غیرفعال شد.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}