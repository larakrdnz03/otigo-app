package com.otigo.auth_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otigo.auth_api.entity.FcmToken;
import com.otigo.auth_api.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByUser(UserEntity user);

    List<FcmToken> findAllByUser(UserEntity user);
}