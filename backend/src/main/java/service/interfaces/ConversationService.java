package service.interfaces;

import model.Advertisement;
import model.Conversation;
import model.Message;
import model.User;

import java.util.List;

public interface ConversationService {

    Conversation startConversation(User buyer, Advertisement advertisement);

    Conversation getById(int conversationId);

    List<Conversation> getUserConversations(int userId);

    void closeConversation(int conversationId, User user);

    Message sendMessage(int conversationId, User sender, String content);

    List<Message> getMessages(int conversationId);

}