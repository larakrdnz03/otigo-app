package com.otigo.auth_api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SymptomSurveyRepository extends JpaRepository<SymptomSurvey, Long> {

    /**
     * Bir çocuğa ait tüm anket sonuçlarını, anket tarihine göre
     * en yeniden eskiye doğru (Desc) sıralı olarak getirir.
     * * Uzmanın, çocuğun "geçmiş anketlerini" incelemesi için bu sorgu kullanılacak.
     */
    List<SymptomSurvey> findByChildOrderBySurveyDateDesc(Child child);

    /**
     * Bir velinin doldurduğu tüm anketleri getirir (gerekirse).
     */
    List<SymptomSurvey> findByParentOrderBySurveyDateDesc(UserEntity user);
}