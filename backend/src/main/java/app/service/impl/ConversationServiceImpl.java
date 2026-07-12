package app.service.impl;

import app.model.Advertisement;
import app.model.Conversation;
import app.model.Message;
import app.model.User;
import app.repository.interfaces.ConversationRepository;
import app.repository.interfaces.MessageRepository;
import app.repository.interfaces.AdvertisementRepository;
import app.service.interfaces.ConversationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AdvertisementRepository advertisementRepository;

    public ConversationServiceImpl(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            AdvertisementRepository advertisementRepository) {

        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.advertisementRepository = advertisementRepository;
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
    public List<Conversation> getUserConversations(User user) {

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        return conversationRepository.findByUserId(user.getId());
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
    public Message sendMessage(
            User sender,
            User receiver,
            int advertisementId,
            String content) {

        if (sender == null || receiver == null) {
            throw new RuntimeException("Invalid users.");
        }

        Advertisement advertisement =
                advertisementRepository.findById(advertisementId);

        if (advertisement == null) {
            throw new RuntimeException("Advertisement not found.");
        }

        Conversation conversation =
                conversationRepository.findByUsersAndAdvertisement(
                        sender.getId(),
                        receiver.getId(),
                        advertisementId);

        if (conversation == null) {

            conversation = new Conversation();
            conversation.setBuyer(sender);
            conversation.setSeller(receiver);
            conversation.setAdvertisement(advertisement);
            conversation.setClosed(false);

            conversation = conversationRepository.save(conversation);
        }

        if (conversation.isClosed()) {
            throw new RuntimeException("Conversation is closed.");
        }

        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSender(sender);
        message.setContent(content);
        message.setTime(LocalDateTime.now());

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