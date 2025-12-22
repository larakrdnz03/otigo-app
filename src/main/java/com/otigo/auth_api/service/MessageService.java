// MessageService.java
package com.otigo.auth_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.entity.Message;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.MessageRepository;
import com.otigo.auth_api.repository.UserRepository;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender; // E-posta göndermek için

    public MessageService(MessageRepository messageRepository, 
                          UserRepository userRepository,
                          JavaMailSender mailSender) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public Message sendMessage(UserEntity sender, Long receiverId, String content) {
        UserEntity receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Alıcı bulunamadı."));

        // 1. Mesajı Kaydet
        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent(content);
        msg.setSentAt(LocalDateTime.now());
        Message savedMsg = messageRepository.save(msg);

        // 2. Bildirim Gönder (Gereksinim 2.6)
        sendNotificationEmail(receiver, sender, content);

        return savedMsg;
    }

    private void sendNotificationEmail(UserEntity receiver, UserEntity sender, String content) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(receiver.getEmail());
            mail.setSubject("Yeni Mesajınız Var! - Otigo");
            mail.setText("Merhaba,\n\n" + 
                         sender.getEmail() + " size bir mesaj gönderdi:\n\n" + 
                         "\"" + content + "\"\n\n" +
                         "Uygulamaya girip cevaplayabilirsiniz.");
            
            mailSender.send(mail);
        } catch (Exception e) {
            // E-posta hatası mesajlaşmayı durdurmamalı, sadece loglayabiliriz.
            System.err.println("Bildirim e-postası gönderilemedi: " + e.getMessage());
        }
    }

    public List<Message> getChatHistory(Long currentUserId, Long otherUserId) {
        return messageRepository.findChatHistory(currentUserId, otherUserId);
    }
}