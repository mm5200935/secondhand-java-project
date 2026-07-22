package app.repository;

import app.model.City;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class CityRepository {
    private static final String DATA_FILE = "src/main/resources/data/cities.ser";
    private Map<Long, City> cities;
    private AtomicLong idGenerator;

    public CityRepository() {
        cities = new HashMap<>();
        idGenerator = new AtomicLong(1);
        loadData();
        if (cities.isEmpty()) {
            createInitialCities();
        }
    }

    public City save(City city) {
        if (city.getId() == null) {
            city.setId(idGenerator.getAndIncrement());
        }
        cities.put(city.getId(), city);
        saveData();
        return city;
    }

    public Optional<City> findById(Long id) {
        return Optional.ofNullable(cities.get(id));
    }

    public List<City> findAll() {
        return new ArrayList<>(cities.values());
    }

    public void delete(Long id) {
        cities.remove(id);
        saveData();
    }

    private void createInitialCities() {
        save(new City(null, "تهران", "تهران"));
        save(new City(null, "مشهد", "خراسان رضوی"));
        save(new City(null, "اصفهان", "اصفهان"));
        save(new City(null, "شیراز", "فارس"));
        save(new City(null, "تبریز", "آذربایجان شرقی"));
        save(new City(null, "کرج", "البرز"));
        save(new City(null, "قم", "قم"));
        save(new City(null, "رشت", "گیلان"));
        save(new City(null, "اهواز", "خوزستان"));
        save(new City(null, "کرمانشاه", "کرمانشاه"));
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) ois.readObject();
            cities = (Map<Long, City>) data.get("cities");
            idGenerator = new AtomicLong((Long) data.get("idGenerator"));
        } catch (Exception e) {
            System.err.println("Error loading city data: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();

            Map<String, Object> data = new HashMap<>();
            data.put("cities", cities);
            data.put("idGenerator", idGenerator.get());

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Error saving city data: " + e.getMessage());
        }
    }
}