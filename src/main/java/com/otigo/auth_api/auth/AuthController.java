package com.otigo.auth_api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.request.VerifyCodeRequest;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
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

    @PostMapping("/check-code")
    public ResponseEntity<String> checkCode(@RequestBody VerifyCodeRequest request) {
        boolean isValid = authService.checkVerificationCode(request.getEmail(), request.getCode());
        if (isValid) {
            return ResponseEntity.ok("Kod Doğru");
        } else {
            return ResponseEntity.badRequest().body("Kod Yanlış");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request.getEmail());
            return ResponseEntity.ok("Şifre sıfırlama maili gönderildi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Şifre başarıyla güncellendi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}