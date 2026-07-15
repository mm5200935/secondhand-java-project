package app.dto.response;

import app.model.Conversation;
import java.time.LocalDateTime;

public class ConversationResponse {
    private int id;
    private int adId;
    private int buyerId;
    private int sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageTime;
    private boolean closed;
    private String adTitle;
    private String buyerFullName;
    private String sellerFullName;

    public ConversationResponse(Conversation c) {
        this.id = c.getId();
        this.adId = c.getAdvertisement().getId();
        this.buyerId = c.getBuyer().getId();
        this.sellerId = c.getSeller().getId();
        this.createdAt = c.getCreatedAt();
        this.lastMessageTime = c.getLastMessageAt();
        this.closed = c.isClosed();
        this.adTitle = c.getAdvertisement() != null ? c.getAdvertisement().getTitle() : null;
        this.buyerFullName = c.getBuyer() != null ? c.getBuyer().getFullName() : null;
        this.sellerFullName = c.getSeller() != null ? c.getSeller().getFullName() : null;
    }

    public int getId() { return id; }
    public int getAdId() { return adId; }
    public int getBuyerId() { return buyerId; }
    public int getSellerId() { return sellerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public boolean isClosed() { return closed; }

    public String getAdTitle() { return adTitle; }
    public String getBuyerFullName() { return buyerFullName; }
    public String getSellerFullName() { return sellerFullName; }
}