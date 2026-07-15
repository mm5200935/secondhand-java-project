package app.repository.interfaces;

import app.model.AdvertisementImage;
import java.util.List;

public interface AdvertisementImageRepository {
    void save(int advertisementId, String imagePath);
    List<AdvertisementImage> findByAdvertisementId(int advertisementId);
    void deleteByAdvertisementId(int advertisementId);
}