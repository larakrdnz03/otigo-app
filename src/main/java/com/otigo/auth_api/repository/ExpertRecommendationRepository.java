package com.otigo.auth_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otigo.auth_api.entity.Activity;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.Expert;
import com.otigo.auth_api.entity.ExpertRecommendation;
import com.otigo.auth_api.entity.UserEntity;

import java.util.List;

@Repository
public interface ExpertRecommendationRepository extends JpaRepository<ExpertRecommendation, Long> {

    /**
     * Bir çocuğa ait tüm uzman önerilerini/yorumlarını, tarihine göre
     * en yeniden eskiye doğru (Desc) sıralı olarak getirir.
     * * Veli'nin "Uzman Yorumları" ekranında bu sorgu kullanılacak.
     */
    //List<ExpertRecommendation> findByChildOrderByRecommendationDateDesc(Child child);

    List<ExpertRecommendation> findByChildOrderByCreatedAtDesc(Child child);

    // Tamamlanmamış (Active) ve bir oyuna bağlı olan görevleri getir
    List<ExpertRecommendation> findByIsCompletedFalseAndActivityIsNotNull();  

    /**
     * Bir uzmanın yaptığı tüm önerileri/yorumları listeler (gerekirse).
     */
    
    List<ExpertRecommendation> findByExpertOrderByCreatedAtDesc(UserEntity expert);

    // 1. Bir çocuğa verilmiş tüm tavsiyeler (Tarih sırasına göre)
    //List<ExpertRecommendation> findByChildOrderByRecommendationDateDesc(Child child);

    // 2. Bir uzmanın verdiği tüm tavsiyeler
    List<ExpertRecommendation> findByExpert(Expert expert);

    // 3. (Analiz için) Belirli bir oyun için verilmiş tüm tavsiyeler
    List<ExpertRecommendation> findByActivity(Activity activity);

    // 4. Çocuğun henüz tamamlamadığı (Aktif) görevler
    List<ExpertRecommendation> findByChildAndIsCompletedFalse(Child child);

    
}