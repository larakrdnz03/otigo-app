package com.otigo.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.service.NotificationService;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Android uygulaması login olduktan sonra FCM token'ını kaydeder.
     * POST /api/notifications/token
     * Body: { "token": "fcm_token_buraya" }
     */
    @PostMapping("/token")
    public ResponseEntity<?> saveToken(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            String token = body.get("token");
            notificationService.saveToken(user, token);
            return ResponseEntity.ok("Token kaydedildi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}