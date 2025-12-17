package com.otigo.auth_api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
    List<ExpertRecommendation> findByIsCompletedFalseAndGameIsNotNull();  

    /**
     * Bir uzmanın yaptığı tüm önerileri/yorumları listeler (gerekirse).
     */
    
    List<ExpertRecommendation> findByExpertOrderByRecommendationDateDesc(UserEntity expert);

    
}