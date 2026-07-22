package app.service.impl;
import app.model.Advertisement;
import app.model.Conversation;
import app.model.Message;
import app.model.User;
import app.repository.interfaces.ConversationRepository;
import app.repository.interfaces.MessageRepository;
import app.service.interfaces.ConversationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationServiceImpl(ConversationRepository conversationRepository,
                                   MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Conversation startConversation(User buyer, Advertisement advertisement) {

        if (buyer == null || advertisement == null) {
            throw new RuntimeException("Invalid data.");
        }

        User seller = advertisement.getOwner();

        Conversation conversation =
                conversationRepository.findByUsersAndAdvertisement(
                        buyer.getId(),
                        seller.getId(),
                        advertisement.getId());

        if (conversation != null) {
            return conversation;
        }

        conversation = new Conversation();
        conversation.setBuyer(buyer);
        conversation.setSeller(seller);
        conversation.setAdvertisement(advertisement);
        conversation.setClosed(false);

        return conversationRepository.save(conversation);
    }

    @Override
    public Conversation getById(int conversationId) {
        return conversationRepository.findById(conversationId);
    }

    @Override
    public List<Conversation> getUserConversations(int userId) {
        return conversationRepository.findByUserId(userId);
    }

    @Override
    public void closeConversation(int conversationId, User user) {

        Conversation conversation =
                conversationRepository.findById(conversationId);

        if (conversation == null) {
            throw new RuntimeException("Conversation not found.");
        }

        if (conversation.getBuyer().getId() != user.getId()
                && conversation.getSeller().getId() != user.getId()) {
            throw new RuntimeException("Access denied.");
        }

        conversation.setClosed(true);
        conversationRepository.update(conversation);
    }

    @Override
    public Message sendMessage(int conversationId,
                               User sender,
                               String content) {

        Conversation conversation =
                conversationRepository.findById(conversationId);

        if (conversation == null) {
            throw new RuntimeException("Conversation not found.");
        }

        if (conversation.isClosed()) {
            throw new RuntimeException("Conversation is closed.");
        }

        if (sender.getId() != conversation.getBuyer().getId()
                && sender.getId() != conversation.getSeller().getId()) {
            throw new RuntimeException("You are not a participant of this conversation.");
        }

        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSender(sender);
        message.setContent(content);
        message.setTime(LocalDateTime.now());

        conversation.setLastMessageAt(message.getSentAt());
        conversationRepository.update(conversation);

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessages(int conversationId) {

        Conversation conversation =
                conversationRepository.findById(conversationId);

        if (conversation == null) {
            throw new RuntimeException("Conversation not found.");
        }

        return messageRepository.findByConversationId(conversationId);
    }
}