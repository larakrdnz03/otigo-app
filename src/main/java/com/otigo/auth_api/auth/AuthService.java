package com.otigo.auth_api.auth;

import com.otigo.auth_api.config.JwtService;
import com.otigo.auth_api.dto.request.VerifyCodeRequest;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.AccountStatus;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.UserRepository;
import com.otigo.auth_api.service.ActivityService;
import com.otigo.auth_api.token.PasswordResetToken;
import com.otigo.auth_api.token.PasswordResetTokenRepository;
import com.otigo.auth_api.token.VerificationToken;
import com.otigo.auth_api.token.VerificationTokenRepository;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository tokenRepository;
    private final ActivityService gameService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final Resend resend;

    public AuthService(UserRepository userRepository,
                       ChildRepository childRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       ActivityService gameService,
                       VerificationTokenRepository tokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       @Value("${RESEND_API_KEY:re_KTTRiU4y_ATLuy6fAASD2dJuTusLFJBK8}") String resendApiKey) {
        this.userRepository = userRepository;
        this.childRepository = childRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.gameService = gameService;
        this.tokenRepository = tokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.resend = new Resend(resendApiKey);
    }

    public LoginResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email adresi zaten kullanılıyor!");
        }

        UserEntity user = new UserEntity();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(null);
        user.setStatus(AccountStatus.PENDING_VERIFICATION);

        userRepository.save(user);

        String verificationCode = generateVerificationCode();
        saveUserVerificationToken(user, verificationCode);
        sendVerificationEmail(user, verificationCode);

        return new LoginResponse(
            "kayit-bekleniyor",
            "kayit-bekleniyor",
            user.getId(),
            null,
            user.getFirstname(),
            user.getLastname()
        );
    }

    public LoginResponse verifyUser(VerifyCodeRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        VerificationToken tokenData = tokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doğrulama kodu bulunamadı"));

        if (!tokenData.getToken().equals(request.getCode())) {
            throw new RuntimeException("Geçersiz doğrulama kodu!");
        }

        if (tokenData.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kodun süresi dolmuş. Lütfen 'Kodu Tekrar Gönder' yapın.");
        }

        if (request.getRole() == null) {
            throw new RuntimeException("Lütfen bir kullanıcı rolü (Veli/Uzman) seçin!");
        }

        user.setRole(request.getRole());
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        tokenData.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(tokenData);

        var jwtToken = jwtService.generateToken(user);
        return new LoginResponse(
            jwtToken,
            "dummy-refresh-token",
            user.getId(),
            user.getRole().name(),
            user.getFirstname(),
            user.getLastname()
        );
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new LoginResponse(
                jwtToken,
                "dummy-refresh-token",
                user.getId(),
                user.getRole().name(),
                user.getFirstname(),
                user.getLastname()
        );
    }

    public void registerChild(Child child) {
        Child savedChild = childRepository.save(child);
        gameService.createInitialActivitiesForChild(savedChild);
        System.out.println("✅ Çocuk kaydedildi ve oyunları oluşturuldu: " + savedChild.getName());
    }

    public void resendVerificationCode(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (user.getStatus() == AccountStatus.ACTIVE) {
            throw new RuntimeException("Hesap zaten doğrulanmış.");
        }

        String newCode = generateVerificationCode();
        saveUserVerificationToken(user, newCode);
        sendVerificationEmail(user, newCode);
    }

    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Bu email ile kayıtlı kullanıcı bulunamadı."));

        passwordResetTokenRepository.findByUser(user)
                .ifPresent(t -> passwordResetTokenRepository.delete(t));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );
        passwordResetTokenRepository.save(resetToken);

        sendPasswordResetEmail(user, token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz veya süresi dolmuş token."));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token süresi dolmuş. Lütfen tekrar şifre sıfırlama talebinde bulunun.");
        }

        if (resetToken.getUsedAt() != null) {
            throw new RuntimeException("Bu token daha önce kullanılmış.");
        }

        UserEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);
    }

    private void saveUserVerificationToken(UserEntity user, String token) {
        VerificationToken verificationToken = tokenRepository.findByUser(user)
                .orElse(new VerificationToken());

        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setCreatedAt(LocalDateTime.now());
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        verificationToken.setConfirmedAt(null);

        tokenRepository.save(verificationToken);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendVerificationEmail(UserEntity user, String code) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("OTIGO Destek <destek@otigo.info>")
                    .to(user.getEmail())
                    .subject("Doğrulama Kodun - Otigo")
                    .text("Merhaba " + user.getFirstname() + ",\n\n" +
                          "Giriş için doğrulama kodun: " + code + "\n\n" +
                          "Bu kodu kimseyle paylaşma.\n\n" +
                          "Sevgiler,\nOTIGO Ekibi")
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            System.out.println("✅ DOĞRULAMA KODU GÖNDERİLDİ: " + code + " | ID: " + response.getId());
        } catch (Exception e) {
            System.err.println("❌ MAİL HATASI: " + e.getMessage());
        }
    }

    private void sendPasswordResetEmail(UserEntity user, String token) {
        try {
            String deepLink = "otigo://reset-password?token=" + token;
            String httpsLink = "https://otigo-app.onrender.com/reset-password?token=" + token;

            String htmlContent = "<p>Merhaba " + user.getFirstname() + ",</p>" +
                    "<p>Şifre sıfırlama talebinde bulundunuz.</p>" +
                    "<p><a href=\"" + deepLink + "\">Şifreyi Sıfırla (Uygulama)</a></p>" +
                    "<p>Uygulama açılmazsa: <a href=\"" + httpsLink + "\">" + httpsLink + "</a></p>" +
                    "<p>Bu link 15 dakika geçerlidir.</p>" +
                    "<p>Eğer bu talebi siz yapmadıysanız bu maili görmezden gelin.</p>" +
                    "<p>Sevgiler,<br>OTIGO Ekibi</p>";

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("OTIGO Destek <destek@otigo.info>")
                    .to(user.getEmail())
                    .subject("Şifre Sıfırlama - Otigo")
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            System.out.println("✅ ŞİFRE SIFIRLAMA MAILI GÖNDERİLDİ: " + user.getEmail() + " | ID: " + response.getId());
        } catch (Exception e) {
            System.err.println("❌ MAİL HATASI: " + e.getMessage());
        }
    }

    public boolean checkVerificationCode(String email, String code) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        VerificationToken tokenData = tokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doğrulama kodu bulunamadı"));

        if (!tokenData.getToken().equals(code)) {
            throw new RuntimeException("Geçersiz doğrulama kodu!");
        }

        if (tokenData.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kodun süresi dolmuş.");
        }

        return true;
    }
}