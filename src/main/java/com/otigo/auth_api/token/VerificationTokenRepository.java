package com.otigo.auth_api.token;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.otigo.auth_api.user.UserEntity;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    // Bize "token" (metin) üzerinden o token nesnesini bulmamızı sağlar
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(UserEntity user);
}