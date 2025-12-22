package com.otigo.auth_api.token;

import org.springframework.data.jpa.repository.JpaRepository;

import com.otigo.auth_api.entity.UserEntity;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Bize "token" (metin) üzerinden o token nesnesini bulmamızı sağlar
    Optional<RefreshToken> findByToken(String token);

    // Bir kullanıcının var olan token'ını silmek için (örn: çıkış yaparken)
    void deleteByUser(UserEntity user);
}