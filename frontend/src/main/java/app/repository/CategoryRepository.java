package app.repository;

import app.model.Category;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class CategoryRepository {
    private static final String DATA_FILE = "src/main/resources/data/categories.ser";
    private Map<Long, Category> categories;
    private AtomicLong idGenerator;

    public CategoryRepository() {
        categories = new HashMap<>();
        idGenerator = new AtomicLong(1);
        loadData();
        if (categories.isEmpty()) {
            createInitialCategories();
        }
    }

    public Category save(Category category) {
        if (category.getId() == null) {
            category.setId(idGenerator.getAndIncrement());
        }
        categories.put(category.getId(), category);
        saveData();
        return category;
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(categories.get(id));
    }

    public List<Category> findAll() {
        return new ArrayList<>(categories.values());
    }

    public List<Category> findRootCategories() {
        return categories.values().stream()
                .filter(c -> c.getParentId() == null)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Category> findChildren(Long parentId) {
        return categories.values().stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void delete(Long id) {
        categories.remove(id);
        saveData();
    }

    private void createInitialCategories() {
        Category electronics = new Category();
        electronics.setName("الکترونیک");
        electronics.setDescription("کالاهای الکترونیکی و دیجیتال");
        save(electronics);

        Category furniture = new Category();
        furniture.setName("مبلمان و دکوراسیون");
        furniture.setDescription("مبلمان، وسایل تزئینی و دکوراسیون");
        save(furniture);

        Category clothing = new Category();
        clothing.setName("پوشاک و اکسسوری");
        clothing.setDescription("لباس، کیف، کفش و اکسسوری");
        save(clothing);

        Category books = new Category();
        books.setName("کتاب و مجله");
        books.setDescription("کتاب، مجله، نشریات و مواد آموزشی");
        save(books);

        Category vehicles = new Category();
        vehicles.setName("خودرو و وسایل نقلیه");
        vehicles.setDescription("خودرو، موتورسیکلت و سایر وسایل نقلیه");
        save(vehicles);
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) ois.readObject();
            categories = (Map<Long, Category>) data.get("categories");
            idGenerator = new AtomicLong((Long) data.get("idGenerator"));
        } catch (Exception e) {
            System.err.println("Error loading category data: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();

            Map<String, Object> data = new HashMap<>();
            data.put("categories", categories);
            data.put("idGenerator", idGenerator.get());

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Error saving category data: " + e.getMessage());
        }
    }
}