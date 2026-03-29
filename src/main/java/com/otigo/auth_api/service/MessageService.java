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
    private final Resend resend;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          @Value("${resend.api.key}") String resendApiKey) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.resend = new Resend(resendApiKey);
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

        sendNotificationEmail(receiver, sender, content);

        return savedMsg;
    }

    private void sendNotificationEmail(UserEntity receiver, UserEntity sender, String content) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("OTIGO Destek <destek@otigo.info>")
                    .to(receiver.getEmail())
                    .subject("Yeni Mesajınız Var! - Otigo")
                    .text("Merhaba,\n\n" +
                          sender.getEmail() + " size bir mesaj gönderdi:\n\n" +
                          "\"" + content + "\"\n\n" +
                          "Uygulamaya girip cevaplayabilirsiniz.\n\n" +
                          "Sevgiler,\nOTIGO Ekibi")
                    .build();

            resend.emails().send(params);
        } catch (Exception e) {
            System.err.println("Bildirim e-postası gönderilemedi: " + e.getMessage());
        }
    }

    public List<Message> getChatHistory(Long currentUserId, Long otherUserId) {
        return messageRepository.findChatHistory(currentUserId, otherUserId);
    }
}