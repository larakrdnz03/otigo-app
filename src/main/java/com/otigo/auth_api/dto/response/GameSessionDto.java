package com.otigo.auth_api.dto.response;

/**
 * Tek bir oyunun (örn: "Gölge-Nesne Eşleştirme") tek bir oturumuna (level'a)
 * ait özet istatistiklerini taşır.
 *
 * Görseldeki grafiğin X eksenindeki her "Oyun N (Lvl N)" çubuğu = bir GameSessionDto
 *
 * Frontend bu listeyi alır:
 * - Kırmızı bar   → mistakesMade (hata sayısı)
 * - Mavi çizgi    → independenceScore (bağımsızlık %)
 * - Etiket        → "Oyun 1 (Lvl 1)" gibi
 */
public class GameSessionDto {

    /** Kaçıncı oturum (1, 2, 3...) - X eksenindeki sıra */
    private int sessionNumber;

    /** Oynanan seviye (currentLevel değeri) */
    private int level;

    /** Hata sayısı - kırmızı bar */
    private int mistakesMade;

    /** Veli yardım sayısı */
    private int parentHelpCount;

    /** Toplam hedef sayısı (o level'daki nesne/seçim sayısı) */
    private int totalTargetCount;

    /**
     * Bağımsızlık skoru (%) - mavi çizgi
     * Formül: (1 - parentHelpCount / totalTargetCount) x 100
     * 0.0 - 100.0 arasında
     */
    private double independenceScore;

    /** Oyunda geçirilen süre (saniye) */
    private int durationSeconds;

    /** Skor */
    private int score;

    /** Tekrar sayısı: Bu level daha önce kaç kez oynandı */
    private int retryCount;

    // --- Constructor ---
    public GameSessionDto() {}

    // --- Builder tarzı static factory (okunabilirlik için) ---
    public static GameSessionDto from(
            int sessionNumber,
            int level,
            int mistakesMade,
            int parentHelpCount,
            int totalTargetCount,
            double independenceScore,
            int durationSeconds,
            int score) {
        GameSessionDto dto = new GameSessionDto();
        dto.sessionNumber = sessionNumber;
        dto.level = level;
        dto.mistakesMade = mistakesMade;
        dto.parentHelpCount = parentHelpCount;
        dto.totalTargetCount = totalTargetCount;
        dto.independenceScore = independenceScore;
        dto.durationSeconds = durationSeconds;
        dto.score = score;
        return dto;
    }

    // --- Getter ve Setter'lar ---
    public int getSessionNumber() { return sessionNumber; }
    public void setSessionNumber(int sessionNumber) { this.sessionNumber = sessionNumber; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getMistakesMade() { return mistakesMade; }
    public void setMistakesMade(int mistakesMade) { this.mistakesMade = mistakesMade; }

    public int getParentHelpCount() { return parentHelpCount; }
    public void setParentHelpCount(int parentHelpCount) { this.parentHelpCount = parentHelpCount; }

    public int getTotalTargetCount() { return totalTargetCount; }
    public void setTotalTargetCount(int totalTargetCount) { this.totalTargetCount = totalTargetCount; }

    public double getIndependenceScore() { return independenceScore; }
    public void setIndependenceScore(double independenceScore) { this.independenceScore = independenceScore; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}