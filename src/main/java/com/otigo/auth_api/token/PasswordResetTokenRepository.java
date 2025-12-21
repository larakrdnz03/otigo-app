package com.otigo.auth_api.token;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Bize "token" (metin) üzerinden o token nesnesini bulmamızı sağlar
    Optional<PasswordResetToken> findByToken(String token);
}