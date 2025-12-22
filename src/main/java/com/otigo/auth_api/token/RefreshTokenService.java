package com.otigo.auth_api.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.otigo.auth_api.entity.UserEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // application.properties'ten 90 GÜNLÜK (uzun) süreyi okuyacağız
    @Value("${application.security.jwt.refresh-token.expiration-ms}")
    private Long refreshTokenExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Verilen token (UUID) string'ine göre veritabanından token'ı bulur.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Bir kullanıcı için yeni bir Refresh Token oluşturur, veritabanına kaydeder
     * ve bu token'ı geri döner.
     */
    public RefreshToken createRefreshToken(UserEntity user) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        // Şu anki zamana 90 günlük süreyi (milisaniye olarak) ekle
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        // Güvenli, rastgele ve benzersiz bir token ID'si oluştur
        refreshToken.setToken(UUID.randomUUID().toString());

        // Veritabanına kaydet
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    /**
     * Bir token'ın süresinin dolup dolmadığını kontrol eder.
     * Eğer süresi dolduysa, veritabanından siler ve bir hata fırlatır.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        // Token'ın son kullanma tarihi, şu anki zamandan ÖNCE mi?
        if (token.getExpiryDate().isBefore(Instant.now())) {
            // Süresi dolmuşsa, veritabanından sil
            refreshTokenRepository.delete(token);
            // Mobil uygulamanın yakalaması için bir hata fırlat
            // (Bu hatayı yakalayan mobil uygulama, kullanıcıyı tekrar login ekranına atmalı)
            throw new RuntimeException(
                    token.getToken() + " Refresh token was expired. Please make a new signin request.");
        }
        
        // Süresi dolmamışsa, token'ın kendisini geri dön
        return token;
    }
    
    /**
     * Bir kullanıcının var olan tüm refresh token'larını siler (Logout için).
     */
    public void deleteByUser(UserEntity user) {
        refreshTokenRepository.deleteByUser(user);
    }
}