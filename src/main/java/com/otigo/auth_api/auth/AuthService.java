package com.otigo.auth_api.auth;

import com.otigo.auth_api.config.JwtService;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.Expert;
import com.otigo.auth_api.entity.Parent;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.AccountStatus;
import com.otigo.auth_api.entity.enums.UserRole;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.UserRepository;
import com.otigo.auth_api.service.ActivityService;
import com.otigo.auth_api.token.VerificationToken;
import com.otigo.auth_api.token.VerificationTokenRepository;
//import com.otigo.auth_api.user.*; 
//import com.otigo.auth_api.controller.*;
//import com.otigo.auth_api.entity.*;
//import com.otigo.auth_api.entity.enums.*;
//import com.otigo.auth_api.dto.request.*;
//import com.otigo.auth_api.dto.response.*;
//import com.otigo.auth_api.repository.*;
//import com.otigo.auth_api.service.*;
//import com.otigo.auth_api.controller.*;
// Child, User, GameService, Repository'ler buradan gelir

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // Tarih işlemleri için
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ChildRepository childRepository; // EKLENDİ: Çocuğu kaydetmek için
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;
    private final VerificationTokenRepository tokenRepository; //token için ekledim
    
    // --- YENİ EKLENEN SERVİS ---
    private final ActivityService gameService; 

    // Constructor Güncellendi (GameService ve ChildRepository eklendi)
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
     * BU METOT: Veli veya Uzman kaydeder (User Tablosu).
     * Burada oyun oluşturulmaz, çünkü oyunlar çocuklar içindir.
     */
    public LoginResponse register(RegisterRequest request) {

        String incomingRole = request.getRole().toUpperCase();
        
        UserEntity userToSave = null; // Ortak atayı tutacak referans
       //userRepository.save(userToSave);  //kullanıcıyı kaydettik

        
        // 2. Role göre doğru nesneyi oluştur (Factory Mantığı)
        if (incomingRole.equals("VELI")) {
            Parent parent = new Parent();
            // Parent'a özel alanlar varsa burada set edilebilir
            // Örn: parent.setCocukSayisi(0); 
            parent.setRole(UserRole.VELI); // String'i Enum'a çevirmiş olduk
            userToSave = parent;

        } else if (incomingRole.equals("UZMAN")) {
            Expert expert = new Expert();
            // Expert'e özel alanlar varsa burada set edilebilir
            // Örn: expert.setDiplomaNo("...");
            expert.setRole(UserRole.UZMAN);
            userToSave = expert;

        } else {
            // Güvenlik: Eğer geçersiz bir rol gelirse hata fırlat
            throw new RuntimeException("Geçersiz rol! Lütfen VELI veya UZMAN seçiniz.");
        }

        UserEntity user = new UserEntity();
        /*user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        //user.setRole(request.getRole());
        user.setStatus(AccountStatus.PENDING_VERIFICATION);*/

        userToSave.setFirstname(request.getFirstname());
        userToSave.setLastname(request.getLastname());
        userToSave.setEmail(request.getEmail());
        userToSave.setPassword(passwordEncoder.encode(request.getPassword()));
        userToSave.setStatus(AccountStatus.PENDING_VERIFICATION);

        //userRepository.save(user); // Kullanıcıyı kaydet
        userRepository.save(userToSave);

        String verificationCode = generateVerificationCode(); 
        //sendVerificationEmail(user, verificationCode);
        sendVerificationEmail(userToSave, verificationCode);
        // a) Token'ı veritabanına kaydet
        saveUserVerificationToken(userToSave, verificationCode);

        var jwtToken = jwtService.generateToken(userToSave);
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
        gameService.createInitialActivitiesForChild(savedChild);
        
        System.out.println("✅ Çocuk kaydedildi ve oyunları oluşturuldu: " + savedChild.getName());
    }

    /**
     * [YENİ METOT] KODU TEKRAR GÖNDER
     * Kullanıcı "Kodu Tekrar Gönder" butonuna bastığında burası çalışır.
     */
    public void resendVerificationCode(String email) {
        // Kullanıcıyı bul
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Zaten onaylıysa işlem yapma
        if (user.getStatus() == AccountStatus.ACTIVE) {
            throw new RuntimeException("Hesap zaten doğrulanmış.");
        }

        // Yeni kod üret
        String newCode = generateVerificationCode();
        
        // Token tablosunu güncelle (Eskisi varsa günceller, yoksa yeni açar)
        saveUserVerificationToken(user, newCode);
        
        // Mail at
        sendVerificationEmail(user, newCode);
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

    /**
     * [EKSİK OLAN METOT] 
     * Bunu dosyanın en altına, diğer metodların dışına yapıştır.
     */
    private void saveUserVerificationToken(UserEntity user, String token) {
        // 1. Kullanıcının eski token'ı var mı kontrol et
        VerificationToken verificationToken = tokenRepository.findByUser(user)
                .orElse(new VerificationToken()); // Yoksa yeni (boş) bir tane oluştur

        // 2. Token nesnesinin içini doldur
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setCreatedAt(java.time.LocalDateTime.now());
        
        // Token 15 dakika geçerli olsun
        verificationToken.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(15)); 
        
        verificationToken.setConfirmedAt(null); // Yeni kod olduğu için onaylanmadı sayıyoruz

        // 3. Veritabanına kaydet
        tokenRepository.save(verificationToken);
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