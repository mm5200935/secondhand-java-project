package model;
import java.time.LocalDateTime;

public class Message {

    private int id;
    private User sender;
    private String content;
    private LocalDateTime sentAt;
    private boolean seen;

    public Message() {
        sentAt = LocalDateTime.now();
        seen = false;
    }

    public Message(int id,
                   User sender,
                   String content) {

        this();
        this.id = id;
        this.sender = sender;
        this.content = content;
    }

    public void setConversationId(int id) {
        this.id = id;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setContent(String cont) {
        this.content = cont;
    }

    public void setTime(LocalDateTime time) {
        this.sentAt = time;
    }


}