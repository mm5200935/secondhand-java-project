package app.repository;

import app.model.Message;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MessageRepository {
    private static final String DATA_FILE = "src/main/resources/data/messages.ser";
    private Map<Long, Message> messages;
    private AtomicLong idGenerator;

    public MessageRepository() {
        messages = new HashMap<>();
        idGenerator = new AtomicLong(1);
        loadData();
    }

    public Message save(Message message) {
        if (message.getId() == null) {
            message.setId(idGenerator.getAndIncrement());
        }
        messages.put(message.getId(), message);
        saveData();
        return message;
    }

    public Optional<Message> findById(Long id) {
        return Optional.ofNullable(messages.get(id));
    }

    public List<Message> findByConversationId(Long conversationId) {
        return messages.values().stream()
                .filter(m -> m.getConversationId().equals(conversationId))
                .sorted(Comparator.comparing(Message::getSentAt))
                .collect(Collectors.toList());
    }

    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    public void delete(Long id) {
        messages.remove(id);
        saveData();
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) ois.readObject();
            messages = (Map<Long, Message>) data.get("messages");
            idGenerator = new AtomicLong((Long) data.get("idGenerator"));
        } catch (Exception e) {
            System.err.println("Error loading message data: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();

            Map<String, Object> data = new HashMap<>();
            data.put("messages", messages);
            data.put("idGenerator", idGenerator.get());

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Error saving message data: " + e.getMessage());
        }
    }
}