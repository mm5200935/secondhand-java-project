package app.dto;

public class MessageRequest {
    private int receiverId;
    private int advertisementId;
    private String content;

    // Getters and Setters
    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public int getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(int advertisementId) { this.advertisementId = advertisementId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}