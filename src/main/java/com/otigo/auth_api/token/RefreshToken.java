package com.otigo.auth_api.token;

import com.otigo.auth_api.user.UserEntity;
import jakarta.persistence.*;
import java.time.Instant; // LocalDateTime yerine Instant kullanmak daha standarttır

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token'ı (JWT'yi değil, rastgele bir string'i) saklayacağız
    // ve bu token'ın kendisini de eşsiz yapacağız.
    @Column(nullable = false, unique = true)
    private String token;

    // Bu token hangi kullanıcıya ait
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;

    // Token'ın son geçerlilik tarihi
    @Column(nullable = false)
    private Instant expiryDate;

    // --- Constructor'lar, Getter ve Setter'lar ---

    public RefreshToken() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }
}