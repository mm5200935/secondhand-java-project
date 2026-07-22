package app.dto.request;

import java.io.Serializable;

public class RatingRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long adId;
    private Long raterId;
    private int score;
    private String comment;

    // Getters and Setters
    public Long getAdId() { return adId; }
    public void setAdId(Long adId) { this.adId = adId; }

    public Long getRaterId() { return raterId; }
    public void setRaterId(Long raterId) { this.raterId = raterId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}