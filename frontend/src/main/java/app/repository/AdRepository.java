package app.repository;

import app.model.Ad;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class AdRepository {
    private static final String DATA_FILE = "src/main/resources/data/ads.ser";
    private Map<Long, Ad> ads;
    private AtomicLong idGenerator;

    public AdRepository() {
        ads = new HashMap<>();
        idGenerator = new AtomicLong(1);
        loadData();
    }

    public Ad save(Ad ad) {
        if (ad.getId() == null) {
            ad.setId(idGenerator.getAndIncrement());
        }
        ad.setUpdatedAt(new Date());
        ads.put(ad.getId(), ad);
        saveData();
        return ad;
    }

    public Optional<Ad> findById(Long id) {
        return Optional.ofNullable(ads.get(id));
    }

    public List<Ad> findAll() {
        return new ArrayList<>(ads.values());
    }

    public List<Ad> findByOwnerId(Long ownerId) {
        return ads.values().stream()
                .filter(a -> a.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Ad> findActiveAds() {
        return ads.values().stream()
                .filter(Ad::isVisibleToPublic)
                .collect(Collectors.toList());
    }

    public List<Ad> findPendingAds() {
        return ads.values().stream()
                .filter(a -> a.getStatus() == Ad.AdStatus.PENDING)
                .collect(Collectors.toList());
    }

    public List<Ad> findByCategoryId(Long categoryId) {
        return ads.values().stream()
                .filter(a -> a.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public List<Ad> findByCityId(Long cityId) {
        return ads.values().stream()
                .filter(a -> a.getCityId().equals(cityId))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        ads.remove(id);
        saveData();
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) ois.readObject();
            ads = (Map<Long, Ad>) data.get("ads");
            idGenerator = new AtomicLong((Long) data.get("idGenerator"));
        } catch (Exception e) {
            System.err.println("Error loading ad data: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();

            Map<String, Object> data = new HashMap<>();
            data.put("ads", ads);
            data.put("idGenerator", idGenerator.get());

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Error saving ad data: " + e.getMessage());
        }
    }
}