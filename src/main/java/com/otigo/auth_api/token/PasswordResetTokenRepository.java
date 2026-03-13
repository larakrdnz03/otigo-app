package com.otigo.auth_api.token;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.otigo.auth_api.entity.UserEntity;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(UserEntity user);
}