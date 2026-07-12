package app.repository.interfaces;

import app.enums.AdvertisementStatus;
import app.model.Advertisement;

import java.util.List;

public interface AdvertisementRepository {

    Advertisement save(Advertisement advertisement);

    Advertisement update(Advertisement advertisement);

    void updateStatus(int advertisementId, AdvertisementStatus status);

    void delete(int advertisementId);

    Advertisement findById(int advertisementId);

    List<Advertisement> findAll();

    List<Advertisement> findByUserId(int userId);

    List<Advertisement> findByCategoryId(int categoryId);

    List<Advertisement> findByCityId(int cityId);

    List<Advertisement> findByStatus(AdvertisementStatus status);

    List<Advertisement> search(String keyword);
}