package app.service.impl;

import app.enums.AdvertisementStatus;
import app.enums.SortType;
import app.exception.ResourceNotFoundException;
import app.exception.UnauthorizedException;
import app.model.Advertisement;
import app.model.User;
import app.repository.interfaces.AdvertisementRepository;
import app.repository.interfaces.CategoryRepository;
import app.repository.interfaces.CityRepository;
import app.service.interfaces.AdvertisementService;
import org.springframework.stereotype.Service;
import app.repository.interfaces.AdvertisementImageRepository;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final AdvertisementImageRepository advertisementImageRepository;

    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository,
                                    CategoryRepository categoryRepository,
                                    CityRepository cityRepository,
                                    AdvertisementImageRepository advertisementImageRepository) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.advertisementImageRepository = advertisementImageRepository;
    }



    @Override
    public Advertisement create(Advertisement ad, User owner, List<String> imagePaths) {

        if (owner == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        if (categoryRepository.findById(ad.getCategory().getId()) == null) {
            throw new ResourceNotFoundException("Category not found.");
        }

        if (cityRepository.findById(ad.getCity().getId()) == null) {
            throw new ResourceNotFoundException("City not found.");
        }

        ad.setOwner(owner);
        ad.setStatus(AdvertisementStatus.PENDING);

        Advertisement saved = advertisementRepository.save(ad);

        if (imagePaths != null) {
            for (String path : imagePaths) {
                advertisementImageRepository.save(saved.getId(), path);
            }
        }

        saved.setImages(advertisementImageRepository.findByAdvertisementId(saved.getId()));
        return saved;
    }



    @Override
    public Advertisement update(Advertisement ad, User owner, List<String> imagePaths) {

        if (owner == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        Advertisement existing = advertisementRepository.findById(ad.getId());

        if (existing == null) {
            throw new ResourceNotFoundException("Advertisement not found.");
        }

        if (existing.getStatus() == AdvertisementStatus.DELETED) {
            throw new ResourceNotFoundException("Advertisement has been deleted.");
        }

        if (existing.getOwner() == null) {
            throw new ResourceNotFoundException("Advertisement owner not found.");
        }

        if (existing.getOwner().getId() != owner.getId()) {
            throw new UnauthorizedException("You are not the owner of this advertisement.");
        }

        if (categoryRepository.findById(ad.getCategory().getId()) == null) {
            throw new ResourceNotFoundException("Category not found.");
        }

        if (cityRepository.findById(ad.getCity().getId()) == null) {
            throw new ResourceNotFoundException("City not found.");
        }

        existing.setTitle(ad.getTitle());
        existing.setDescription(ad.getDescription());
        existing.setPrice(ad.getPrice());
        existing.setNegotiable(ad.isNegotiable());

        existing.setCategory(ad.getCategory());
        existing.setCity(ad.getCity());

        Advertisement updated = advertisementRepository.update(existing);

        if (imagePaths != null) {
            advertisementImageRepository.deleteByAdvertisementId(updated.getId());
            for (String path : imagePaths) {
                advertisementImageRepository.save(updated.getId(), path);
            }
        }

        updated.setImages(advertisementImageRepository.findByAdvertisementId(updated.getId()));
        return updated;
    }



    @Override
    public void delete(int adId, User owner) {

        if (owner == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad == null) {
            throw new ResourceNotFoundException("Advertisement not found.");
        }

        if (ad.getOwner() == null) {
            throw new ResourceNotFoundException("Advertisement owner not found.");
        }

        if (ad.getOwner().getId() != owner.getId()) {
            throw new UnauthorizedException("You are not the owner.");
        }

        advertisementRepository.updateStatus(
                ad.getId(),
                AdvertisementStatus.DELETED
        );
    }



    @Override
    public Advertisement getById(int id) {

        Advertisement ad = advertisementRepository.findById(id);

        if (ad == null) {
            throw new ResourceNotFoundException("Advertisement not found.");
        }

        return ad;
    }



    @Override
    public List<Advertisement> getAll() {
        return advertisementRepository.findAll()
                .stream()
                .filter(ad -> ad.getStatus() == AdvertisementStatus.ACTIVE)
                .collect(Collectors.toList());
    }



    @Override
    public void approve(int adId) {

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad == null) {
            throw new ResourceNotFoundException("Advertisement not found.");
        }

        advertisementRepository.updateStatus(
                ad.getId(),
                AdvertisementStatus.ACTIVE
        );
    }



    @Override
    public void reject(int adId, String reason) {

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad == null) {
            throw new ResourceNotFoundException("Advertisement not found.");
        }

        advertisementRepository.rejectAdvertisement(ad.getId(), reason);
    }



    @Override
    public void markAsSold(int adId) {

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad == null) {
            throw new ResourceNotFoundException("Advertisement not found.");
        }

        advertisementRepository.updateStatus(
                ad.getId(),
                AdvertisementStatus.SOLD
        );
    }



    @Override
    public void deactivate(int adId) {

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad == null) {
            throw new ResourceNotFoundException("Advertisement not found.");
        }

        advertisementRepository.updateStatus(
                ad.getId(),
                AdvertisementStatus.DELETED
        );
    }



    @Override
    public List<Advertisement> search(String keyword, Integer categoryId, Integer cityId,
                                      Double minPrice, Double maxPrice) {

        String search = keyword == null ? "" : keyword.toLowerCase();

        return advertisementRepository.findAll()
                .stream()
                .filter(ad -> ad.getStatus() == AdvertisementStatus.ACTIVE)
                .filter(ad ->
                        ad.getTitle().toLowerCase().contains(search) ||
                                ad.getDescription().toLowerCase().contains(search))
                .filter(ad -> categoryId == null || ad.getCategoryId() == categoryId)
                .filter(ad -> cityId == null || ad.getCityId() == cityId)
                .filter(ad -> minPrice == null || ad.getPrice() >= minPrice)
                .filter(ad -> maxPrice == null || ad.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> searchForAdmin(String keyword, Integer categoryId, Integer cityId,
                                              Double minPrice, Double maxPrice) {

        String search = keyword == null ? "" : keyword.toLowerCase();

        return advertisementRepository.findAll()
                .stream()
                .filter(ad ->
                        ad.getTitle().toLowerCase().contains(search) ||
                                ad.getDescription().toLowerCase().contains(search))
                .filter(ad -> categoryId == null || ad.getCategoryId() == categoryId)
                .filter(ad -> cityId == null || ad.getCityId() == cityId)
                .filter(ad -> minPrice == null || ad.getPrice() >= minPrice)
                .filter(ad -> maxPrice == null || ad.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }



    @Override
    public List<Advertisement> filterByCategory(int categoryId) {
        return advertisementRepository.findByCategoryId(categoryId)
                .stream()
                .filter(ad -> ad.getStatus() == AdvertisementStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> filterByCity(int cityId) {
        return advertisementRepository.findByCityId(cityId)
                .stream()
                .filter(ad -> ad.getStatus() == AdvertisementStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> filterByUser(int userId) {
        return advertisementRepository.findByUserId(userId);
    }

    @Override
    public List<Advertisement> getByStatus(AdvertisementStatus status) {
        return advertisementRepository.findByStatus(status);
    }



    @Override
    public List<Advertisement> sort(List<Advertisement> ads, SortType sortType) {

        if (ads == null) {
            return List.of();
        }

        switch (sortType) {

            case NEWEST:
                return ads.stream()
                        .sorted(Comparator.comparing(Advertisement::getCreatedAt).reversed())
                        .collect(Collectors.toList());

            case OLDEST:
                return ads.stream()
                        .sorted(Comparator.comparing(Advertisement::getCreatedAt))
                        .collect(Collectors.toList());

            case LOWEST_PRICE:
                return ads.stream()
                        .sorted(Comparator.comparing(Advertisement::getPrice))
                        .collect(Collectors.toList());

            case HIGHEST_PRICE:
                return ads.stream()
                        .sorted(Comparator.comparing(Advertisement::getPrice).reversed())
                        .collect(Collectors.toList());

            default:
                return ads;
        }
    }

    @Override
    public List<Advertisement> getAllForAdmin() {
        return advertisementRepository.findAll();
    }

    @Override
    public void adminDelete(int adId) {
        Advertisement ad = advertisementRepository.findById(adId);
        if (ad == null) {
            throw new ResourceNotFoundException("Advertisement not found.");
        }
        advertisementRepository.updateStatus(ad.getId(), AdvertisementStatus.DELETED);
    }
}