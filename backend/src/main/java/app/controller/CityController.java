package app.controller;

import app.exception.UnauthorizedException;
import app.model.City;
import app.repository.interfaces.CityRepository;
import app.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityRepository cityRepository;

    public CityController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    // GET /api/cities -> لیست تمام شهرها (بدون نیاز به لاگین)
    @GetMapping
    public List<City> getAll() {
        return cityRepository.findAll();
    }

    // POST /api/cities -> ساخت شهر جدید (فقط ادمین)
    @PostMapping
    public City create(@RequestBody City city,
                       @AuthenticationPrincipal AuthenticatedUser principal) {

        requireAdmin(principal);

        if (city.getName() == null || city.getName().isBlank()) {
            throw new RuntimeException("نام شهر نمی‌تواند خالی باشد.");
        }

        if (cityRepository.existsByName(city.getName())) {
            throw new RuntimeException("این شهر از قبل وجود دارد.");
        }

        return cityRepository.save(city);
    }

    // PUT /api/cities/{id} -> ویرایش شهر (فقط ادمین)
    @PutMapping("/{id}")
    public City update(@PathVariable int id,
                       @RequestBody City city,
                       @AuthenticationPrincipal AuthenticatedUser principal) {

        requireAdmin(principal);
        city.setId(id);
        return cityRepository.update(city);
    }

    // DELETE /api/cities/{id} -> حذف شهر (فقط ادمین)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id,
                       @AuthenticationPrincipal AuthenticatedUser principal) {

        requireAdmin(principal);
        cityRepository.delete(id);
    }

    private void requireAdmin(AuthenticatedUser principal) {
        if (principal == null || !"ADMIN".equalsIgnoreCase(principal.role())) {
            throw new UnauthorizedException("Admins only.");
        }
    }
}