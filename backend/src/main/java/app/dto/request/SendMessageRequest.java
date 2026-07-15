package app.dto.request;

public class SendMessageRequest {
    private int adId;
    private String content;
    public int getAdId() { return adId; }
    public void setAdId(int adId) { this.adId = adId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}