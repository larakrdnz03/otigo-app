package com.otigo.auth_api.user;

import jakarta.validation.constraints.NotEmpty;

/**
 * Bu bir DTO'dur. Uzmanın, bir çocuk hakkında
 * "öneri/yorum" bırakırken göndereceği veriyi taşır.
 */
public class CreateRecommendationRequest {

    /**
     * Uzmanın yazdığı asıl öneri/yorum metni.
     */
    @NotEmpty(message = "Öneri/yorum metni boş olamaz")
    private String recommendationText;

    // --- YENİ EKLENEN ALANLAR ---
    
    // Hangi oyun oynanacak? (Boş olabilir, sadece sözel tavsiye ise null gelir)
    private Long activityId;

    // Hedef seviye kaç? (Boş olabilir)
    private Integer targetLevel;

    // --- CONSTRUCTOR (Boş) ---
    public CreateRecommendationRequest() {
    }

    // --- Getter ve Setter ---

    public String getRecommendationText() {
        return recommendationText;
    }

    public void setRecommendationText(String recommendationText) {
        this.recommendationText = recommendationText;
    }

    // Yeni Getter/Setter'lar (Hata veren yerler buraları arıyor):
    
    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }
}