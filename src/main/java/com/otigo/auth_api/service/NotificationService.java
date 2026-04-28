package com.otigo.auth_api.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.otigo.auth_api.entity.FcmToken;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.FcmTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NotificationService {

    private final FcmTokenRepository fcmTokenRepository;

    public NotificationService(FcmTokenRepository fcmTokenRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
    }

    /**
     * Kullanıcının FCM token'ını kaydeder veya günceller.
     */
    @Transactional
    public void saveToken(UserEntity user, String token) {
        FcmToken fcmToken = fcmTokenRepository.findByUser(user)
                .orElse(new FcmToken());

        fcmToken.setUser(user);
        fcmToken.setToken(token);
        fcmToken.setUpdatedAt(LocalDateTime.now());
        fcmTokenRepository.save(fcmToken);
    }

    /**
     * Kullanıcıya push notification gönderir.
     */
    public void sendNotification(UserEntity receiver, String title, String body) {
        Optional<FcmToken> fcmTokenOpt = fcmTokenRepository.findByUser(receiver);

        if (fcmTokenOpt.isEmpty()) {
            System.out.println("⚠️ FCM token bulunamadı: " + receiver.getEmail());
            return;
        }

        String token = fcmTokenOpt.get().getToken();

        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(token)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Bildirim gönderildi: " + response);
        } catch (Exception e) {
            System.err.println("❌ Bildirim gönderilemedi: " + e.getMessage());
        }
    }
}