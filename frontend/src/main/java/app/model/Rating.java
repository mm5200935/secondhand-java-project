package app.model;

import java.io.Serializable;
import java.util.Date;

public class Rating implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long adId;
    private Long raterId;
    private Long sellerId;
    private int score;
    private String comment;
    private Date createdAt;
    private String raterFullName;

    public Rating() {
        this.createdAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdId() { return adId; }
    public void setAdId(Long adId) { this.adId = adId; }

    public Long getRaterId() { return raterId; }
    public void setRaterId(Long raterId) { this.raterId = raterId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getRaterFullName() { return raterFullName; }
    public void setRaterFullName(String raterFullName) { this.raterFullName = raterFullName; }



}