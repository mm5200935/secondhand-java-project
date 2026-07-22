package app.dto.response;

import app.model.Rating;

import java.time.LocalDateTime;

public class RatingResponse {

    private int id;
    private int adId;
    private int raterId;
    private int sellerId;
    private int score;
    private String comment;
    private LocalDateTime createdAt;
    private String raterFullName;

    public RatingResponse(Rating rating) {
        this.id = rating.getId();
        this.adId = rating.getAdvertisement() != null ? rating.getAdvertisement().getId() : 0;
        this.raterId = rating.getBuyer() != null ? rating.getBuyer().getId() : 0;
        this.sellerId = rating.getSeller() != null ? rating.getSeller().getId() : 0;
        this.score = rating.getScore();
        this.comment = rating.getComment();
        this.createdAt = rating.getCreatedAt();
        this.raterId = rating.getBuyer() != null ? rating.getBuyer().getId() : 0;
        this.raterFullName = rating.getBuyer() != null ? rating.getBuyer().getFullName() : null;
    }

    public int getId() { return id; }
    public int getAdId() { return adId; }
    public int getRaterId() { return raterId; }
    public int getSellerId() { return sellerId; }
    public int getScore() { return score; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getRaterFullName() { return raterFullName; }
}