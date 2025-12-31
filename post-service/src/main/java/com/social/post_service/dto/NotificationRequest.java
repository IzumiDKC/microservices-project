package com.social.post_service.dto;

public class NotificationRequest {
    private Long recipientId;
    private Long senderId;
    private String senderName;
    private String content;
    private Long postId;

    public NotificationRequest() {
    }

    public NotificationRequest(Long recipientId, Long senderId, String senderName, String content, Long postId) {
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.postId = postId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}