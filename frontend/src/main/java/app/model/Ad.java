package app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ad implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String description;
    private double price;
    private Long ownerId;
    private Long categoryId;
    private Long cityId;
    private AdStatus status;
    private Date createdAt;
    private Date updatedAt;
    private List<String> imagePaths;
    private List<Long> conversationIds;
    private String rejectionReason;
    private String ownerFullName;
    private String ownerUsername;
    private String ownerStatus;

    public enum AdStatus {
        PENDING, ACTIVE, REJECTED, SOLD, DELETED
    }

    public Ad() {
        this.imagePaths = new ArrayList<>();
        this.conversationIds = new ArrayList<>();
        this.status = AdStatus.PENDING;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }

    public AdStatus getStatus() { return status; }
    public void setStatus(AdStatus status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }

    public List<Long> getConversationIds() { return conversationIds; }
    public void setConversationIds(List<Long> conversationIds) { this.conversationIds = conversationIds; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public boolean isVisibleToPublic() {
        return status == AdStatus.ACTIVE || status == AdStatus.SOLD;
    }

    public boolean isEditableByUser(Long userId) {
        return ownerId.equals(userId) && status != AdStatus.SOLD && status != AdStatus.DELETED;
    }

    public String getOwnerFullName() { return ownerFullName; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerFullName(String ownerFullName) { this.ownerFullName = ownerFullName; }

    public String getOwnerStatus() { return ownerStatus; }
    public void setOwnerStatus(String ownerStatus) { this.ownerStatus = ownerStatus; }
}