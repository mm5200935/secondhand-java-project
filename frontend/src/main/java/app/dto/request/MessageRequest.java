package app.dto.request;

import java.io.Serializable;

public class MessageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long adId;
    private Long senderId;
    private String content;

    // Getters and Setters
    public Long getAdId() { return adId; }
    public void setAdId(Long adId) { this.adId = adId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}