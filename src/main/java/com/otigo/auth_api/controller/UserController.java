package com.otigo.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Spring Security'den geliyor
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.otigo.auth_api.service.UserService;

@RestController
@RequestMapping("/api/v1/user") // DİKKAT: Burası /auth/ değil, /user/
public class UserController {

    private final UserService userService;

    // UserService'i buraya enjekte ediyoruz
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * SADECE giriş yapmış kullanıcının KENDİ hesabını dondurmasını sağlar.
     * /api/v1/user/deactivate adresine POST isteği atılır.
     */
    @PostMapping("/deactivate")
    public ResponseEntity<String> deactivateAccount(Authentication authentication) {
        // 'Authentication' nesnesi, Spring Security tarafından sağlanır
        // ve o an giriş yapmış kullanıcının bilgilerini (e-postasını) taşır.
        
        try {
            // "authentication.getName()" bize o an giriş yapmış kullanıcının e-postasını verir
            String userEmail = authentication.getName();
            
            userService.deactivateAccount(userEmail);
            
            return ResponseEntity.ok("Hesabınız başarıyla donduruldu. Çıkış yapılıyor...");
        
        } catch (UsernameNotFoundException e) {
            // Bu normalde olmamalı (giriş yapmış kullanıcı bulunamazsa)
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı.");
        }
    }

    /**
     * SADECE giriş yapmış kullanıcının KENDİ hesabını silmesini sağlar.
     * /api/v1/user/delete adresine POST isteği atılır.
     */
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
}