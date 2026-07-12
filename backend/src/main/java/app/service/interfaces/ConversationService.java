package app.service.interfaces;

import app.model.Advertisement;
import app.model.Conversation;
import app.model.Message;
import app.model.User;

import java.util.List;

public interface ConversationService {

    /**
     * Starts a new conversation for an advertisement.
     */
    Conversation startConversation(User buyer, Advertisement advertisement);

    /**
     * Returns a conversation by its id.
     */
    Conversation getById(int conversationId);

    /**
     * Returns all conversations for a user.
     */
    List<Conversation> getUserConversations(User user);

    /**
     * Closes a conversation.
     */
    void closeConversation(int conversationId, User user);

    /**
     * Sends a message.
     * If a conversation between the buyer and seller for the advertisement
     * does not exist, it should be created automatically.
     */
    Message sendMessage(
            User sender,
            User receiver,
            int advertisementId,
            String content
    );

    /**
     * Returns all messages of a conversation.
     */
    List<Message> getMessages(int conversationId);
}