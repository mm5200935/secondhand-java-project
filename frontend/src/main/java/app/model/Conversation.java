package app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Conversation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long adId;
    private Long buyerId;
    private Long sellerId;
    private Date createdAt;
    private Date lastMessageTime;
    private List<Long> messageIds;
    private String adTitle;
    private String buyerFullName;
    private String sellerFullName;

    public Conversation() {
        this.messageIds = new ArrayList<>();
        this.createdAt = new Date();
        this.lastMessageTime = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdId() { return adId; }
    public void setAdId(Long adId) { this.adId = adId; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(Date lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public List<Long> getMessageIds() { return messageIds; }
    public void setMessageIds(List<Long> messageIds) { this.messageIds = messageIds; }

    public String getAdTitle() { return adTitle; }
    public void setAdTitle(String adTitle) { this.adTitle = adTitle; }

    public String getBuyerFullName() { return buyerFullName; }
    public void setBuyerFullName(String buyerFullName) { this.buyerFullName = buyerFullName; }

    public String getSellerFullName() { return sellerFullName; }
    public void setSellerFullName(String sellerFullName) { this.sellerFullName = sellerFullName; }


}