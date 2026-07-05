package app.service.interfaces;

import app.enums.AdvertisementStatus;
import app.model.Advertisement;
import app.model.User;
import app.enums.SortType;

import java.util.List;

public interface AdvertisementService {

    Advertisement create(Advertisement ad, User owner);

    Advertisement update(Advertisement ad, User owner);

    void delete(int adId, User owner);

    Advertisement getById(int id);

    List<Advertisement> getAll();

    void approve(int adId);

    void reject(int adId);

    void markAsSold(int adId);

    void deactivate(int adId);

    List<Advertisement> search(String keyword);

    List<Advertisement> filterByCategory(int categoryId);

    List<Advertisement> filterByCity(int cityId);

    List<Advertisement> filterByUser(int userId);

    List<Advertisement> sort(List<Advertisement> ads, SortType sortType);

    List<Advertisement> getByStatus(AdvertisementStatus status);

}