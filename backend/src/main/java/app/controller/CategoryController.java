package app.controller;

import app.exception.UnauthorizedException;
import app.model.Advertisement;
import app.model.Category;
import app.repository.interfaces.AdvertisementRepository;
import app.repository.interfaces.CategoryRepository;
import app.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final AdvertisementRepository advertisementRepository;

    public CategoryController(CategoryRepository categoryRepository,
                              AdvertisementRepository advertisementRepository) {
        this.categoryRepository = categoryRepository;
        this.advertisementRepository = advertisementRepository;
    }

    // GET /api/categories -> لیست تمام دسته‌بندی‌ها (بدون نیاز به لاگین)
    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    // POST /api/categories -> ساخت دسته‌بندی جدید (فقط ادمین)
    @PostMapping
    public Category create(@RequestBody Category category,
                           @AuthenticationPrincipal AuthenticatedUser principal) {

        requireAdmin(principal);

        if (category.getName() == null || category.getName().isBlank()) {
            throw new RuntimeException("نام دسته‌بندی نمی‌تواند خالی باشد.");
        }

        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("این دسته‌بندی از قبل وجود دارد.");
        }

        return categoryRepository.save(category);
    }

    // PUT /api/categories/{id} -> ویرایش دسته‌بندی (فقط ادمین)
    @PutMapping("/{id}")
    public Category update(@PathVariable int id,
                           @RequestBody Category category,
                           @AuthenticationPrincipal AuthenticatedUser principal) {

        requireAdmin(principal);
        category.setId(id);
        return categoryRepository.update(category);
    }

    // DELETE /api/categories/{id} -> حذف دسته‌بندی (فقط ادمین)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id,
                       @AuthenticationPrincipal AuthenticatedUser principal) {

        requireAdmin(principal);

        List<Advertisement> ads = advertisementRepository.findByCategoryId(id);
        for (Advertisement ad : ads) {
            advertisementRepository.delete(ad.getId());
        }

        categoryRepository.delete(id);
    }

    private void requireAdmin(AuthenticatedUser principal) {
        if (principal == null || !"ADMIN".equalsIgnoreCase(principal.role())) {
            throw new UnauthorizedException("Admins only.");
        }
    }
}