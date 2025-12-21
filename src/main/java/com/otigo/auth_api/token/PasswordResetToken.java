package com.otigo.auth_api.token;

import com.otigo.auth_api.user.UserEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY) // Bir kullanıcının birden fazla sıfırlama talebi olabilir
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt; // Bu token'ın ne zaman kullanıldığı

    public PasswordResetToken() {
        // JPA için boş constructor
    }

    public PasswordResetToken(String token, UserEntity user, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.token = token;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // --- Getter ve Setter'lar ---

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
}