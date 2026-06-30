package repository.interfaces;

import model.Message;

import java.util.List;

public interface MessageRepository {

    Message save(Message message);

    Message update(Message message);

    void delete(int messageId);

    Message findById(int messageId);

    List<Message> findAll();

    List<Message> findByConversationId(int conversationId);

    List<Message> findBySenderId(int senderId);

}