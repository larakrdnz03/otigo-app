package com.otigo.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/deactivate")
    public ResponseEntity<String> deactivateAccount(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            userService.deactivateAccount(userEmail);
            return ResponseEntity.ok("Hesabınız başarıyla donduruldu. Çıkış yapılıyor...");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı.");
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            userService.deleteAccount(userEmail);
            return ResponseEntity.ok("Hesabınız başarıyla silindi. Verileriniz anonimleştirildi.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı.");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            String currentPassword = body.get("currentPassword");
            String newPassword = body.get("newPassword");
            userService.changePassword(userEmail, currentPassword, newPassword);
            return ResponseEntity.ok("Şifre başarıyla güncellendi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}