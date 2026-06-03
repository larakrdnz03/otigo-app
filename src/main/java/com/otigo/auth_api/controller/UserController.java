package com.otigo.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.UserRole;
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
            userService.deactivateAccount(authentication.getName());
            return ResponseEntity.ok("Hesabınız başarıyla donduruldu.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı.");
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        try {
            userService.deleteAccount(authentication.getName());
            return ResponseEntity.ok("Hesabınız başarıyla silindi.");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı.");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            userService.changePassword(
                    authentication.getName(),
                    body.get("currentPassword"),
                    body.get("newPassword")
            );
            return ResponseEntity.ok("Şifre başarıyla güncellendi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Profil güncelle (telefon, adres).
     * PUT /api/v1/user/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            userService.updateProfile(user, body.get("phoneNumber"), body.get("address"));
            return ResponseEntity.ok("Profil güncellendi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Mevcut kullanıcı bilgilerini getir.
     * GET /api/v1/user/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "firstname", user.getFirstname() != null ? user.getFirstname() : "",
                "lastname", user.getLastname() != null ? user.getLastname() : "",
                "email", user.getEmail(),
                "role", user.getRole() != null ? user.getRole().name() : "",
                "phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                "address", user.getAddress() != null ? user.getAddress() : "",
                "profilePhoto", user.getProfilePhoto() != null ? user.getProfilePhoto() : ""
        ));
    }

    /**
     * Profil fotoğrafı yükle (Base64).
     * POST /api/v1/user/profile-photo
     * Body: { "photo": "base64_string" }
     */
    @PostMapping("/profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            userService.updateProfilePhoto(user, body.get("photo"));
            return ResponseEntity.ok("Profil fotoğrafı güncellendi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}