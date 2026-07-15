package app.service.interfaces;

import app.model.Advertisement;
import app.model.Conversation;
import app.model.Message;
import app.model.User;

import java.util.List;

public interface ConversationService {

    Conversation startConversation(User buyer, Advertisement advertisement);

    Conversation getById(int conversationId);

    List<Conversation> getUserConversations(int userId);

    void closeConversation(int conversationId, User user);

    Message sendMessage(int conversationId, User sender, String content);

    List<Message> getMessages(int conversationId);

}