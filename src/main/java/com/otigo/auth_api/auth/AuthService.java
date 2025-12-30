package com.otigo.auth_api.auth;

import com.otigo.auth_api.config.JwtService;
//import com.otigo.auth_api.auth.LoginRequest;
//import com.otigo.auth_api.auth.RegisterRequest;
//import com.otigo.auth_api.auth.LoginResponse;
//import com.otigo.auth_api.dto.request.LoginRequest;
//import com.otigo.auth_api.dto.request.RegisterRequest;
import com.otigo.auth_api.dto.request.VerifyCodeRequest;
//import com.otigo.auth_api.dto.response.LoginResponse;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.AccountStatus;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.UserRepository;
import com.otigo.auth_api.service.ActivityService;
import com.otigo.auth_api.token.VerificationToken;
import com.otigo.auth_api.token.VerificationTokenRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;
    private final VerificationTokenRepository tokenRepository;
    private final ActivityService gameService;

    public AuthService(UserRepository userRepository,
                       ChildRepository childRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       JavaMailSender mailSender,
                       ActivityService gameService,
                       VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.childRepository = childRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
        this.gameService = gameService;
        this.tokenRepository = tokenRepository;
    }

    /**
     * 1. ADIM: KAYIT OL (ROL YOK)
     * Kullanıcı sadece İsim, Soyisim, Email, Şifre girer.
     * Rol bilgisi (VELI/UZMAN) burada alınmaz, NULL bırakılır.
     */
    public LoginResponse register(RegisterRequest request) {

        // Email zaten var mı kontrolü
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email adresi zaten kullanılıyor!");
        }

        // Kullanıcıyı oluştur
        UserEntity user = new UserEntity();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // DİKKAT: Rol henüz seçilmediği için null veya varsayılan bir değer atanır.
        user.setRole(null); 
        user.setStatus(AccountStatus.PENDING_VERIFICATION); // Henüz doğrulanmadı

        // Kaydet
        userRepository.save(user);

        // Doğrulama kodu üret ve gönder
        String verificationCode = generateVerificationCode();
        saveUserVerificationToken(user, verificationCode);
        sendVerificationEmail(user, verificationCode);

        // Geçici token dön (Henüz yetkileri kısıtlı olabilir)
        //var jwtToken = jwtService.generateToken(user);
        //return new LoginResponse(jwtToken, "dummy-refresh-token");
        //return new LoginResponse("kayit-ok", "kayit-ok");
        return new LoginResponse("kayit-bekleniyor", "kayit-bekleniyor", null, null);
    }

    /**
     * 2. ADIM: DOĞRULA VE ROL ATA
     * Kullanıcı maildeki kodu girer VE EKRANDAN ROLÜNÜ SEÇER.
     * Frontend bize {email, code, role} gönderir.
     */
    public LoginResponse verifyUser(VerifyCodeRequest request) {
        // 1. Kullanıcıyı bul
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // 2. Token verisini çek
        VerificationToken tokenData = tokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doğrulama kodu bulunamadı"));

        // 3. Kod doğru mu?
        if (!tokenData.getToken().equals(request.getCode())) {
            throw new RuntimeException("Geçersiz doğrulama kodu!");
        }

        // 4. Süresi dolmuş mu?
        if (tokenData.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Kodun süresi dolmuş. Lütfen 'Kodu Tekrar Gönder' yapın.");
        }

        // 5. ROL KONTROLÜ VE ATAMASI (YENİ KISIM)
        // VerifyCodeRequest içinde 'role' alanı dolu gelmeli!
        if (request.getRole() == null) {
            throw new RuntimeException("Lütfen bir kullanıcı rolü (Veli/Uzman) seçin!");
        }

        // Kullanıcıya frontend'den gelen rolü ata
        user.setRole(request.getRole());
        
        // 6. Hesabı Aktif Et
        user.setStatus(AccountStatus.ACTIVE);
        
        // Son halini kaydet
        userRepository.save(user);

        // 7. Token'ı kullanıldı olarak işaretle
        tokenData.setConfirmedAt(java.time.LocalDateTime.now());
        tokenRepository.save(tokenData);

        // 8. Artık ROLÜ olan ve AKTİF bir kullanıcı için Token üret
        //var jwtToken = jwtService.generateToken(user);
        //return new LoginResponse(jwtToken, "dummy-refresh-token");
        //return new LoginResponse("kayit-asamasi", "kayit-asamasi");
        var jwtToken = jwtService.generateToken(user);
        
        return new LoginResponse(
            jwtToken, 
            "dummy-refresh-token", 
            user.getId(), 
            user.getRole().name()
        );
    }

    // --- DİĞER METOTLAR ---

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        //return new LoginResponse(jwtToken, "dummy-refresh-token");
        return new LoginResponse(
                jwtToken,
                "dummy-refresh-token",
                user.getId(),           // Frontend burayı alıp saklayacak
                user.getRole().name()   // Frontend buraya bakıp yönlendirme yapacak (EXPERT -> /expert-home)
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

    private void saveUserVerificationToken(UserEntity user, String token) {
        VerificationToken verificationToken = tokenRepository.findByUser(user)
                .orElse(new VerificationToken());

        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setCreatedAt(java.time.LocalDateTime.now());
        verificationToken.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(15));
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
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(user.getEmail());
            email.setSubject("Doğrulama Kodun - Otigo");
            email.setText("Merhaba " + user.getFirstname() + ",\n\n" +
                    "Giriş için doğrulama kodun: " + code + "\n\n" +
                    "Bu kodu kimseyle paylaşma.");
            mailSender.send(email);
            System.out.println("✅ DOĞRULAMA KODU GÖNDERİLDİ: " + code);
        } catch (Exception e) {
            System.err.println("❌ MAİL HATASI: " + e.getMessage());
        }
    }

    /**
     * Sadece kodun doğru olup olmadığını kontrol eder.
     * Veritabanında kalıcı bir değişiklik yapmaz.
     * Frontend'de "Rol Seçme Ekranına" geçiş izni vermek için kullanılır.
     */
    public boolean checkVerificationCode(String email, String code) {
        // 1. Kullanıcıyı bul
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // 2. Token'ı bul
        VerificationToken tokenData = tokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doğrulama kodu bulunamadı"));

        // 3. Kod eşleşiyor mu?
        if (!tokenData.getToken().equals(code)) {
            throw new RuntimeException("Geçersiz doğrulama kodu!");
        }

        // 4. Süre dolmuş mu?
        if (tokenData.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Kodun süresi dolmuş.");
        }

        return true; // Kod doğru!
    }



}