package repository.interfaces;

import model.Conversation;

import java.util.List;

public interface ConversationRepository {

    Conversation save(Conversation conversation);

    Conversation update(Conversation conversation);

    void delete(int conversationId);

    Conversation findById(int conversationId);

    List<Conversation> findAll();

    List<Conversation> findByUserId(int userId);

    Conversation findByUsersAndAdvertisement(int buyerId, int sellerId, int advertisementId);

}