package com.otigo.auth_api.user;

import jakarta.validation.constraints.NotEmpty;

/**
 * Bu bir DTO'dur. Velinin, bir çocuk için Belirti Anketi'ni
 * doldurduğunda backend'e göndereceği JSON verisini taşır.
 */
public class CreateSymptomSurveyRequest {

    /**
     * Anketin cevaplarını esnek bir formatta (JSON) tutar.
     * Mobil uygulama bize örn: "{ \"soru_1\": 5, \"soru_2\": 3, \"notlar\": \"...\" }"
     * şeklinde bir metin gönderir.
     */
    @NotEmpty(message = "Anket sonuçları (JSON) boş olamaz")
    private String surveyResultsJson;

    // --- Getter ve Setter ---

    public String getSurveyResultsJson() {
        return surveyResultsJson;
    }

    public void setSurveyResultsJson(String surveyResultsJson) {
        this.surveyResultsJson = surveyResultsJson;
    }
}