package app.repository;

import app.model.User;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class UserRepository {
    private static final String DATA_FILE = "src/main/resources/data/users.ser";
    private Map<Long, User> users;
    private Map<String, Long> usernameToId;
    private AtomicLong idGenerator;

    public UserRepository() {
        users = new HashMap<>();
        usernameToId = new HashMap<>();
        idGenerator = new AtomicLong(1);
        loadData();
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        usernameToId.put(user.getUsername(), user.getId());
        saveData();
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByUsername(String username) {
        Long id = usernameToId.get(username);
        return id != null ? findById(id) : Optional.empty();
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public List<User> findActiveUsers() {
        return users.values().stream()
                .filter(u -> u.getStatus() == User.UserStatus.ACTIVE)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void delete(Long id) {
        User user = users.remove(id);
        if (user != null) {
            usernameToId.remove(user.getUsername());
            saveData();
        }
    }

    public void updateRating(Long userId, double newAverage, int newCount) {
        findById(userId).ifPresent(user -> {
            user.setAverageRating(newAverage);
            user.setRatingCount(newCount);
            saveData();
        });
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            createInitialData();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) ois.readObject();

            users = (Map<Long, User>) data.get("users");
            usernameToId = (Map<String, Long>) data.get("usernameToId");
            idGenerator = new AtomicLong((Long) data.get("idGenerator"));
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            createInitialData();
        }
    }

    private void saveData() {
        try {
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();

            Map<String, Object> data = new HashMap<>();
            data.put("users", users);
            data.put("usernameToId", usernameToId);
            data.put("idGenerator", idGenerator.get());

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    private void createInitialData() {
        users = new HashMap<>();
        usernameToId = new HashMap<>();
        idGenerator = new AtomicLong(1);

        // Create default admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setFullName("مدیر سیستم");
        admin.setPhoneNumber("09120000000");
        admin.setEmail("admin@system.com");
        admin.setRole(User.Role.ADMIN);
        save(admin);

        // Create a sample regular user
        User user = new User();
        user.setUsername("user1");
        user.setPassword("user123");
        user.setFullName("کاربر نمونه");
        user.setPhoneNumber("09120000001");
        user.setEmail("user1@example.com");
        save(user);
    }
}