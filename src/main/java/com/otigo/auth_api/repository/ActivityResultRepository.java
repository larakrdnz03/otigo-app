package com.otigo.auth_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.enums.ActivityType;

import java.util.List;

@Repository
public interface ActivityResultRepository extends JpaRepository<ActivityResult, Long> {

    // --- MEVCUT SORGULAR (değişmedi) ---

    List<ActivityResult> findByChildOrderByPlayedAtDesc(Child child);

    List<ActivityResult> findByChildAndActivity_TypeOrderByPlayedAtDesc(Child child, ActivityType type);

    // --- YENİ: GÖRSEL ALGI RAPORU SORGULARI ---

    /**
     * Belirli bir çocuğun, belirli bir oyuna ait tüm sonuçlarını getirir.
     * Her seviyedeki hata, süre, bağımsızlık bilgisi bu sorguyla çekilir.
     *
     * Kullanım: "Gölge-Nesne Eşleştirme" oyunundaki tüm oturumlar
     * GET /api/reports/{childId}/visual-perception
     */
    @Query("SELECT ar FROM ActivityResult ar " +
           "WHERE ar.child = :child " +
           "AND ar.activity.name = :gameName " +
           "ORDER BY ar.playedAt ASC")
    List<ActivityResult> findByChildAndGameNameOrderByPlayedAtAsc(
            @Param("child") Child child,
            @Param("gameName") String gameName);

    /**
     * Belirli bir çocuğun, belirli bir aktivite ID'sine ait tüm sonuçlarını getirir.
     * Oyun adı yerine ID ile sorgu - daha performanslı.
     */
    @Query("SELECT ar FROM ActivityResult ar " +
           "WHERE ar.child = :child " +
           "AND ar.activity.id = :activityId " +
           "ORDER BY ar.playedAt ASC")
    List<ActivityResult> findByChildAndActivityIdOrderByPlayedAtAsc(
            @Param("child") Child child,
            @Param("activityId") Long activityId);

    /**
     * Belirli bir çocuğun, belirli oyun isimlerinin (bir kategori) tüm sonuçlarını getirir.
     * Görsel Algı kategorisi = ["Gölge-Nesne Eşleştirme", "Farklı Cisim Bulma", "Doğru Nesneyi Seçme"]
     *
     * Kullanım: Tüm görsel algı oyunlarını tek sorguda çek
     */
    @Query("SELECT ar FROM ActivityResult ar " +
           "WHERE ar.child = :child " +
           "AND ar.activity.name IN :gameNames " +
           "ORDER BY ar.activity.name ASC, ar.playedAt ASC")
    List<ActivityResult> findByChildAndGameNamesOrderByGameAndDate(
            @Param("child") Child child,
            @Param("gameNames") List<String> gameNames);
}