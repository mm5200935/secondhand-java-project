package app.repository;

import app.model.Conversation;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ConversationRepository {
    private static final String DATA_FILE = "src/main/resources/data/conversations.ser";
    private Map<Long, Conversation> conversations;
    private AtomicLong idGenerator;

    public ConversationRepository() {
        conversations = new HashMap<>();
        idGenerator = new AtomicLong(1);
        loadData();
    }

    public Conversation save(Conversation conversation) {
        if (conversation.getId() == null) {
            conversation.setId(idGenerator.getAndIncrement());
        }
        conversations.put(conversation.getId(), conversation);
        saveData();
        return conversation;
    }

    public Optional<Conversation> findById(Long id) {
        return Optional.ofNullable(conversations.get(id));
    }

    public List<Conversation> findAll() {
        return new ArrayList<>(conversations.values());
    }

    public List<Conversation> findByUserId(Long userId) {
        return conversations.values().stream()
                .filter(c -> c.getBuyerId().equals(userId) || c.getSellerId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Conversation> findByAdId(Long adId) {
        return conversations.values().stream()
                .filter(c -> c.getAdId().equals(adId))
                .collect(Collectors.toList());
    }

    public Optional<Conversation> findByAdIdAndBuyerId(Long adId, Long buyerId) {
        return conversations.values().stream()
                .filter(c -> c.getAdId().equals(adId) && c.getBuyerId().equals(buyerId))
                .findFirst();
    }

    public void delete(Long id) {
        conversations.remove(id);
        saveData();
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) ois.readObject();
            conversations = (Map<Long, Conversation>) data.get("conversations");
            idGenerator = new AtomicLong((Long) data.get("idGenerator"));
        } catch (Exception e) {
            System.err.println("Error loading conversation data: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();

            Map<String, Object> data = new HashMap<>();
            data.put("conversations", conversations);
            data.put("idGenerator", idGenerator.get());

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Error saving conversation data: " + e.getMessage());
        }
    }
}