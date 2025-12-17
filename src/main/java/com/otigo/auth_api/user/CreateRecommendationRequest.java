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

    // --- Getter ve Setter ---

    public String getRecommendationText() {
        return recommendationText;
    }

    public void setRecommendationText(String recommendationText) {
        this.recommendationText = recommendationText;
    }
}