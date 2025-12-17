package com.otigo.auth_api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    /**
     * Bir çocuğa ait tüm oyun sonuçlarını, oynanma tarihine göre
     * en yeniden eskiye doğru (Desc) sıralı olarak getirir.
     * * * Mobil uygulamanın "Gelişim Raporu" grafiğini çizmek için
     * * ihtiyaç duyacağı veri sorgusu budur.
     */
    List<GameResult> findByChildOrderByPlayedAtDesc(Child child);
}