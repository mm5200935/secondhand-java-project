package service.impl;
import model.Advertisement;
import model.User;
import enums.AdvertisementStatus;
import enums.SortType;
import repository.interfaces.AdvertisementRepository;
import service.interfaces.AdvertisementService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }



    @Override
    public Advertisement create(Advertisement ad, User owner) {

        ad.setOwner(owner);
        ad.setStatus(AdvertisementStatus.PENDING);

        return advertisementRepository.save(ad);
    }

    @Override
    public Advertisement update(Advertisement ad, User owner) {

        Advertisement existing = advertisementRepository.findById(ad.getId());

        if (existing == null) {
            throw new RuntimeException("Advertisement not found");
        }

        if (existing.getStatus() == AdvertisementStatus.DELETED) {
            throw new RuntimeException("Advertisement is deleted");
        }

        if (existing.getOwner().getId() != owner.getId()) {
            throw new RuntimeException("You are not the owner");
        }

        return advertisementRepository.update(ad);
    }

    @Override
    public void delete(int adId, User owner) {

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad == null) {
            throw new RuntimeException("Advertisement not found");
        }

        if (ad.getOwner().getId() != owner.getId()) {
            throw new RuntimeException("You are not the owner");
        }

        ad.setStatus(AdvertisementStatus.DELETED);
        advertisementRepository.update(ad);
    }

    @Override
    public Advertisement getById(int id) {
        return advertisementRepository.findById(id);
    }

    @Override
    public List<Advertisement> getAll() {
        return advertisementRepository.findAll()
                .stream()
                .filter(ad -> ad.getStatus() != AdvertisementStatus.DELETED)
                .collect(Collectors.toList());
    }


    @Override
    public void approve(int adId) {

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad != null && ad.getStatus() == AdvertisementStatus.PENDING) {
            ad.setStatus(AdvertisementStatus.ACTIVE);
            advertisementRepository.update(ad);
        }
    }

    @Override
    public void reject(int adId) {

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad != null && ad.getStatus() == AdvertisementStatus.PENDING) {
            ad.setStatus(AdvertisementStatus.REJECTED);
            advertisementRepository.update(ad);
        }
    }

    @Override
    public void markAsSold(int adId) {

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad != null && ad.getStatus() == AdvertisementStatus.ACTIVE) {
            ad.setStatus(AdvertisementStatus.SOLD);
            advertisementRepository.update(ad);
        }
    }

    @Override
    public void deactivate(int adId) {

        Advertisement ad = advertisementRepository.findById(adId);

        if (ad != null) {
            ad.setStatus(AdvertisementStatus.DELETED);
            advertisementRepository.update(ad);
        }
    }


    @Override
    public List<Advertisement> search(String keyword) {

        return advertisementRepository.findAll()
                .stream()
                .filter(ad -> ad.getStatus() == AdvertisementStatus.ACTIVE)
                .filter(ad ->
                        ad.getTitle().toLowerCase().contains(keyword.toLowerCase())
                                || ad.getDescription().toLowerCase().contains(keyword.toLowerCase())
                )
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

        switch (sortType) {

            case NEWEST:
                return ads.stream()
                        .sorted(Comparator.comparing(Advertisement::getId).reversed())
                        .collect(Collectors.toList());

            case OLDEST:
                return ads.stream()
                        .sorted(Comparator.comparing(Advertisement::getId))
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
}