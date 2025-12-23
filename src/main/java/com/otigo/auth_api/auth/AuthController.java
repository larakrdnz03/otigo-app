package com.otigo.auth_api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.otigo.auth_api.dto.request.VerifyCodeRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@RequestBody ResendCodeRequest request) {
        try {
            authService.resendVerificationCode(request.getEmail());
            return ResponseEntity.ok("Doğrulama kodu tekrar gönderildi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<LoginResponse> verify(@RequestBody VerifyCodeRequest request) {
        return ResponseEntity.ok(authService.verifyUser(request));
    }

    // Frontend buraya istek atıp "Kod doğru mu?" diye soracak
    @PostMapping("/check-code")
    public ResponseEntity<String> checkCode(@RequestBody VerifyCodeRequest request) {
        // Not: Request içinde sadece email ve code olması yeterli, role null gelebilir.
        boolean isValid = authService.checkVerificationCode(request.getEmail(), request.getCode());
        
        if (isValid) {
            return ResponseEntity.ok("Kod Doğru");
        } else {
            return ResponseEntity.badRequest().body("Kod Yanlış");
        }
    }


    // Refresh Token, Forgot Password gibi metotları şimdilik kaldırdık
    // çünkü test için sadece register lazım.
}