package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Conversation {

    private int id;
    private User buyer;
    private User seller;
    private Advertisement advertisement;
    private List<Message> messages;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private boolean closed;

    public Conversation() {
        messages = new ArrayList<>();
        createdAt = LocalDateTime.now();
        lastMessageAt = LocalDateTime.now();
        closed = false;
    }

    public Conversation(int id,
                        User buyer,
                        User seller,
                        Advertisement advertisement) {

        this();
        this.id = id;
        this.buyer = buyer;
        this.seller = seller;
        this.advertisement = advertisement;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public void setAdvertisementId(int advId) {
        this.advertisement.setAdvId(advId);
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public void setClosed(boolean status) {
        this.closed = status;
    }

    public User getBuyer() {
        return this.buyer;
    }

    public User getSeller() {
        return this.seller;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void setConversationId(int id) {
        this.id = id;
    }




}