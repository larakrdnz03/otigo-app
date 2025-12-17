package com.otigo.auth_api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Bu bir "interface" (arayüz), "class" değil.
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Spring Data JPA'nın sihirli kısmı burası:
    // Sadece bu metot imzasını tanımlayarak, Spring bizim için otomatik olarak
    // "SELECT * FROM users WHERE email = ?" sorgusunu oluşturup çalıştırır.
    Optional<UserEntity> findByEmail(String email);

}