package model;

import java.time.LocalDateTime;

public class Message {

    private int id;
    private int conversationId;
    private User sender;
    private String content;
    private LocalDateTime sentAt;
    private boolean seen;

    public Message() {
        this.sentAt = LocalDateTime.now();
        this.seen = false;
    }

    public Message(int id, int conversationId, User sender, String content) {
        this();
        this.id = id;
        this.conversationId = conversationId;
        this.sender = sender;
        this.content = content;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }


    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }


    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setTime(LocalDateTime time) {
        this.sentAt = time;
    }
}