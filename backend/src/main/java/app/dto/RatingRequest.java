package app.dto;

public class RatingRequest {
    private int sellerId;
    private int score; // عددی بین ۱ تا ۵
    private String comment;

    // Getters and Setters
    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}