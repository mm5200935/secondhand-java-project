package app.dto.response;

import app.model.Advertisement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Shape sent to the frontend for a single advertisement.
 * Field names intentionally match the frontend's app.model.Ad class
 * (id, title, description, price, ownerId, categoryId, cityId, status, ...)
 * so Jackson can deserialize it there with zero extra mapping code.
 */
public class AdvertisementResponse {

    private int id;
    private String title;
    private String description;
    private double price;
    private int ownerId;
    private String ownerFullName;
    private String ownerUsername;
    private int categoryId;
    private int cityId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean negotiable;
    private List<String> imagePaths;
    private String rejectionReason;
    private String ownerStatus;

    public AdvertisementResponse(Advertisement ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.ownerId = ad.getOwner() != null ? ad.getOwner().getId() : 0;
        this.categoryId = ad.getCategoryId();
        this.cityId = ad.getCityId();
        this.status = ad.getStatus().name();
        this.createdAt = ad.getCreatedAt();
        this.updatedAt = ad.getUpdatedAt();
        this.negotiable = ad.isNegotiable();
        this.imagePaths = ad.getImages() == null ? List.of() :
                ad.getImages().stream()
                        .map(app.model.AdvertisementImage::getFilePath)
                        .collect(Collectors.toList());

        this.ownerId = ad.getOwner() != null ? ad.getOwner().getId() : 0;
        this.ownerFullName = ad.getOwner() != null ? ad.getOwner().getFullName() : null;
        this.ownerUsername = ad.getOwner() != null ? ad.getOwner().getUsername() : null;
        this.ownerStatus = ad.getOwner() != null ? ad.getOwner().getStatus().name() : null;
        this.rejectionReason = ad.getRejectionReason();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getOwnerId() { return ownerId; }
    public int getCategoryId() { return categoryId; }
    public int getCityId() { return cityId; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isNegotiable() { return negotiable; }
    public List<String> getImagePaths() { return imagePaths; }
    public String getOwnerFullName() { return ownerFullName; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getRejectionReason() { return rejectionReason; }
    public String getOwnerStatus() { return ownerStatus; }
}