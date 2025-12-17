package com.otigo.auth_api.config; // veya com.otigo.auth_api.security

import com.otigo.auth_api.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // application.properties'ten gizli anahtarı okur
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    
    // application.properties'ten geçerlilik süresini okur
    @Value("${application.security.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Bir kullanıcı nesnesinden JWT token üretir.
     */
    public String generateToken(UserEntity user) {
        // Token'ın içine gömmek istediğimiz ekstra bilgiler (claims)
        // Buraya kullanıcının rolünü (role) ekliyoruz, bu çok faydalıdır.
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name()); // örn: "PARENT" veya "EXPERT"
        // İsterseniz "userId: user.getId()" de ekleyebilirsiniz.
        
        return buildToken(claims, user.getEmail(), jwtExpirationMs);
    }

    /**
     * Token oluşturma işlemini yapan asıl metot
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims) // Eklediğimiz ekstra bilgiler (örn: rol)
                .setSubject(subject) // Token'ın konusu (bizim için e-posta)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Başlangıç tarihi
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Bitiş tarihi
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // İmza algoritması ve anahtar
                .compact(); // Token'ı oluşturup String'e çevir
    }

    /**
     * properties'ten okuduğumuz Base64 formatındaki gizli anahtarı
     * 'Key' nesnesine dönüştürür.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    
    // ----- TOKEN'I DOĞRULAMAK İÇİN GEREKLİ METOTLAR -----
    // (Bu metotları login'den SONRA, SecurityConfig'i güncellerken kullanacağız,
    // ama şimdiden burada dursunlar.)

    /**
     * Token'dan e-posta (subject) bilgisini çeker.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Token'dan tüm bilgileri (claims) çeker.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Token'dan belirli bir bilgiyi (claim) çekmek için generik bir metot.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Token geçerli mi? (E-posta kontrolü ve süre kontrolü)
     */
    public boolean isTokenValid(String token, UserEntity user) {
        final String email = extractEmail(token);
        return (email.equals(user.getEmail()) && !isTokenExpired(token));
    }

    /**
     * Token'ın süresi dolmuş mu?
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Token'dan son geçerlilik tarihini çeker.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}