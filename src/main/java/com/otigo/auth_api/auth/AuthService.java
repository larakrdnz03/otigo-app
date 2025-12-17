package com.otigo.auth_api.auth;

import com.otigo.auth_api.config.JwtService;
import com.otigo.auth_api.user.*; // Child, User, GameService, Repository'ler buradan gelir

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
    private final ChildRepository childRepository; // EKLENDİ: Çocuğu kaydetmek için
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;
    
    // --- YENİ EKLENEN SERVİS ---
    private final GameService gameService; 

    // Constructor Güncellendi (GameService ve ChildRepository eklendi)
    public AuthService(UserRepository userRepository,
                       ChildRepository childRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       JavaMailSender mailSender,
                       GameService gameService) {
        this.userRepository = userRepository;
        this.childRepository = childRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
        this.gameService = gameService;
    }

    /**
     * BU METOT: Veli veya Uzman kaydeder (User Tablosu).
     * Burada oyun oluşturulmaz, çünkü oyunlar çocuklar içindir.
     */
    public LoginResponse register(RegisterRequest request) {
        UserEntity user = new UserEntity();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(AccountStatus.PENDING_VERIFICATION);

        userRepository.save(user); // Kullanıcıyı kaydet

        String verificationCode = generateVerificationCode(); 
        sendVerificationEmail(user, verificationCode);

        var jwtToken = jwtService.generateToken(user);
        return new LoginResponse(jwtToken, "dummy-refresh-token");
    }

    /**
     * --- [YENİ METOT] ÇOCUK KAYDI BURADA YAPILIR ---
     * Bu metodu bir Controller'dan (Örn: ParentController) çağıracaksın.
     * Veli panelinden "Çocuk Ekle" dendiğinde burası çalışacak.
     */
    public void registerChild(Child child) {
        // 1. Çocuğu veritabanına kaydet
        Child savedChild = childRepository.save(child);

        // 2. --- İŞTE O KRİTİK DOKUNUŞ --- 
        // Çocuk kaydedildiği an, sistemdeki tüm oyunları Level 1 olarak ona tanımlar.
        gameService.createInitialGamesForChild(savedChild);
        
        System.out.println("✅ Çocuk kaydedildi ve oyunları oluşturuldu: " + savedChild.getName());
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
        return new LoginResponse(jwtToken, "dummy-refresh-token");
    }

    // --- YARDIMCI METOTLAR ---

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
}