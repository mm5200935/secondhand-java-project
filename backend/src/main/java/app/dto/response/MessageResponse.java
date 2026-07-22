package app.dto.response;

import app.model.Message;
import java.time.LocalDateTime;

public class MessageResponse {
    private int id;
    private int conversationId;
    private int senderId;
    private String content;
    private LocalDateTime sentAt;

    public MessageResponse(Message m) {
        this.id = m.getId();
        this.conversationId = m.getConversationId();
        this.senderId = m.getSender().getId();
        this.content = m.getContent();
        this.sentAt = m.getSentAt();
    }

    public int getId() { return id; }
    public int getConversationId() { return conversationId; }
    public int getSenderId() { return senderId; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
}