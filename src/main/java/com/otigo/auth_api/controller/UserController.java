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
     * Mevcut kullanıcı bilgilerini getir (DB'den taze).
     * GET /api/v1/user/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        UserEntity user = userService.findByEmail(authentication.getName());
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
     * Kendi profil fotoğrafını getir.
     * GET /api/v1/user/me/profile-photo
     */
    @GetMapping("/me/profile-photo")
    public ResponseEntity<?> getMyProfilePhoto(Authentication authentication) {
        UserEntity user = userService.findByEmail(authentication.getName());
        String photo = user.getProfilePhoto() != null ? user.getProfilePhoto() : "";
        return ResponseEntity.ok(Map.of("profilePhoto", photo));
    }

    /**
     * Başka kullanıcının profil fotoğrafını getir.
     * GET /api/v1/user/{userId}/profile-photo
     */
    @GetMapping("/{userId}/profile-photo")
    public ResponseEntity<?> getUserProfilePhoto(
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            UserEntity photo = userService.findById(userId);
            String photoData = photo.getProfilePhoto() != null ? photo.getProfilePhoto() : "";
            return ResponseEntity.ok(Map.of("profilePhoto", photoData));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Profil fotoğrafı yükle (Base64 JSON veya multipart).
     * POST /api/v1/user/profile-photo
     * POST /api/v1/user/me/profile-photo
     */
    @PostMapping({"/profile-photo", "/me/profile-photo"})
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestBody(required = false) Map<String, String> body,
            @RequestParam(value = "photo", required = false) String photoParam,
            @RequestParam(value = "file", required = false) String fileParam,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();

            String photo = null;
            if (body != null && body.get("photo") != null) {
                photo = body.get("photo");
            } else if (body != null && body.get("profilePhotoBase64") != null) {
                photo = body.get("profilePhotoBase64");
            } else if (photoParam != null) {
                photo = photoParam;
            } else if (fileParam != null) {
                photo = fileParam;
            }

            if (photo == null) {
                return ResponseEntity.badRequest().body("Fotoğraf verisi bulunamadı.");
            }

            userService.updateProfilePhoto(user, photo);
            return ResponseEntity.ok(Map.of("profilePhoto", photo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}