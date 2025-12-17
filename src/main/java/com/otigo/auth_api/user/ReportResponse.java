package com.otigo.auth_api.user; // veya com.otigo.auth_api.report

import java.util.List;
import java.util.Map;

import com.otigo.auth_api.user.expert.ExpertRecommendation;

/**
 * Bu bir DTO'dur. "Gelişim Raporu" için backend'de analiz ettiğimiz
 * tüm verileri (Oyun Sonuçları Analizi, Anket Listesi, Uzman Yorumları)
 * tek bir paket olarak mobil uygulamaya (frontend) göndermemizi sağlar.
 */
public class ReportResponse {

    // 1. OYUN SONUÇLARI
    // Mobil uygulamanın grafik çizmesi için ham (ama sıralı) liste
    private List<GameResult> gameResultsHistory;

    // 2. OYUN SONUÇLARI ANALİZİ (Örnek)
    // Oyun adına göre ortalama hata sayısı
    // (örn: {"Renk Eşleştirme": 5.2, "Ses Tanıma": 8.1})
    private Map<String, Double> averageMistakesByGame;
    
    // (Buraya başka analizler de eklenebilir:
    // private double overallMistakeTrend;
    // private Map<HelpLevel, Long> helpLevelCounts;
    // )

    // 3. ANKET GEÇMİŞİ
    // Çocuğa ait tüm geçmiş anketler
    private List<SymptomSurvey> surveyHistory;

    // 4. UZMAN YORUMLARI
    // Çocuğa ait tüm geçmiş uzman yorumları
    private List<ExpertRecommendation> recommendationHistory;

    
    // --- Getter ve Setter'lar ---
    // (Tüm alanlar için getter ve setter'ları ekleyin)

    public List<GameResult> getGameResultsHistory() {
        return gameResultsHistory;
    }

    public void setGameResultsHistory(List<GameResult> gameResultsHistory) {
        this.gameResultsHistory = gameResultsHistory;
    }

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