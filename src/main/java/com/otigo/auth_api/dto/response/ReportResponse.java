package com.otigo.auth_api.dto.response; 

import java.util.List;
import java.util.Map;

import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.entity.ExpertRecommendation;
import com.otigo.auth_api.entity.SymptomSurvey;

/**
 * Gelişim Raporu DTO'su.
 * GÜNCELLEME: Oyun ve Etkinlik sonuçları iki ayrı liste haline getirildi.
 */
public class ReportResponse {

    // 1. OYUN SONUÇLARI (Sadece 'OYUN' tipindekiler)
    // Grafik çizimi için kullanılır.
    private List<ActivityResult> gameResultsHistory;

    // 2. ETKİNLİK SONUÇLARI (YENİ EKLENDİ 🚀)
    // Sadece 'ETKINLIK' tipindekiler (örn: Hikaye Dinleme) buraya gelecek.
    // Mobil uygulama bunu ayrı bir liste veya grafik olarak gösterecek.
    private List<ActivityResult> eventResultsHistory; 

    // 3. OYUN ANALİZİ
    // Sadece oyunlar için hata ortalaması (Etkinliklerde hata analizi farklı olabilir)
    private Map<String, Double> averageMistakesByGame;
    
    // 4. ANKET GEÇMİŞİ
    private List<SymptomSurvey> surveyHistory;

    // 5. UZMAN YORUMLARI
    private List<ExpertRecommendation> recommendationHistory;

    
    // --- Getter ve Setter'lar ---

    public List<ActivityResult> getGameResultsHistory() {
        return gameResultsHistory;
    }

    public void setGameResultsHistory(List<ActivityResult> gameResultsHistory) {
        this.gameResultsHistory = gameResultsHistory;
    }

    // --- YENİ GETTER / SETTER ---
    public List<ActivityResult> getEventResultsHistory() {
        return eventResultsHistory;
    }

    public void setEventResultsHistory(List<ActivityResult> eventResultsHistory) {
        this.eventResultsHistory = eventResultsHistory;
    }
    // ----------------------------

    public Map<String, Double> getAverageMistakesByGame() {
        return averageMistakesByGame;
    }

    public void setAverageMistakesByGame(Map<String, Double> averageMistakesByGame) {
        this.averageMistakesByGame = averageMistakesByGame;
    }

    public List<SymptomSurvey> getSurveyHistory() {
        return surveyHistory;
    }

    public void setSurveyHistory(List<SymptomSurvey> surveyHistory) {
        this.surveyHistory = surveyHistory;
    }

    public List<ExpertRecommendation> getRecommendationHistory() {
        return recommendationHistory;
    }

    public void setRecommendationHistory(List<ExpertRecommendation> recommendationHistory) {
        this.recommendationHistory = recommendationHistory;
    }
}