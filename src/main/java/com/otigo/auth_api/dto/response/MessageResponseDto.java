package com.otigo.auth_api.dto.response;

import com.otigo.auth_api.entity.Message;
import java.time.LocalDateTime;

public class MessageResponseDto {

    private Long id;
    private Long senderId;
    private String senderEmail;
    private String senderName;
    private Long receiverId;
    private String receiverEmail;
    private String receiverName;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;

    public static MessageResponseDto from(Message message) {
        MessageResponseDto dto = new MessageResponseDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderEmail(message.getSender().getEmail());
        dto.setSenderName(message.getSender().getFirstname() + " " + message.getSender().getLastname());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverEmail(message.getReceiver().getEmail());
        dto.setReceiverName(message.getReceiver().getFirstname() + " " + message.getReceiver().getLastname());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setRead(message.isRead());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}