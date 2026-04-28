package com.otigo.auth_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.entity.Message;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.MessageRepository;
import com.otigo.auth_api.repository.UserRepository;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Message sendMessage(UserEntity sender, Long receiverId, String content) {
        UserEntity receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Alıcı bulunamadı."));

        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent(content);
        msg.setSentAt(LocalDateTime.now());
        Message savedMsg = messageRepository.save(msg);

        // Push notification gönder
        String senderName = sender.getFirstname() != null ? sender.getFirstname() : sender.getEmail();
        notificationService.sendNotification(
                receiver,
                "Yeni Mesaj - " + senderName,
                content.length() > 50 ? content.substring(0, 50) + "..." : content
        );

        return savedMsg;
    }

    public List<Message> getChatHistory(Long currentUserId, Long otherUserId) {
        return messageRepository.findChatHistory(currentUserId, otherUserId);
    }
}