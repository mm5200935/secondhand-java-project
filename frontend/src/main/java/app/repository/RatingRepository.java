package app.repository;

import app.model.Rating;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class RatingRepository {
    private static final String DATA_FILE = "src/main/resources/data/ratings.ser";
    private Map<Long, Rating> ratings;
    private AtomicLong idGenerator;

    public RatingRepository() {
        ratings = new HashMap<>();
        idGenerator = new AtomicLong(1);
        loadData();
    }

    public Rating save(Rating rating) {
        if (rating.getId() == null) {
            rating.setId(idGenerator.getAndIncrement());
        }
        ratings.put(rating.getId(), rating);
        saveData();
        return rating;
    }

    public Optional<Rating> findById(Long id) {
        return Optional.ofNullable(ratings.get(id));
    }

    public List<Rating> findBySellerId(Long sellerId) {
        return ratings.values().stream()
                .filter(r -> r.getSellerId().equals(sellerId))
                .collect(Collectors.toList());
    }

    public List<Rating> findByAdId(Long adId) {
        return ratings.values().stream()
                .filter(r -> r.getAdId().equals(adId))
                .collect(Collectors.toList());
    }

    public Optional<Rating> findByAdIdAndRaterId(Long adId, Long raterId) {
        return ratings.values().stream()
                .filter(r -> r.getAdId().equals(adId) && r.getRaterId().equals(raterId))
                .findFirst();
    }

    public List<Rating> findAll() {
        return new ArrayList<>(ratings.values());
    }

    public void delete(Long id) {
        ratings.remove(id);
        saveData();
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) ois.readObject();
            ratings = (Map<Long, Rating>) data.get("ratings");
            idGenerator = new AtomicLong((Long) data.get("idGenerator"));
        } catch (Exception e) {
            System.err.println("Error loading rating data: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();

            Map<String, Object> data = new HashMap<>();
            data.put("ratings", ratings);
            data.put("idGenerator", idGenerator.get());

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Error saving rating data: " + e.getMessage());
        }
    }
}