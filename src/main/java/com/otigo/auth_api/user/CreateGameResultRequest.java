package com.otigo.auth_api.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Bu bir DTO'dur. Mobil uygulamanın, bir oyun bittiğinde
 * backend'e göndereceği "Oyun Sonucu" verisini taşır.
 */
public class CreateGameResultRequest {

    @NotEmpty(message = "Oyun adı boş olamaz")
    private String gameName; // Örn: "Renk Eşleştirme"

    @NotNull(message = "Skor boş olamaz")
    private Integer score; // Skor (Tamsayı)

    private int gameDurationSeconds; // Oyun ne kadar sürdü (saniye)
    
    private int mistakesMade; // Çocuk ne kadar hata yaptı
    
    private boolean parentHelped; // Veli yardım etti mi?
    
    private HelpLevel parentHelpLevel; // Ne kadar yardım etti? (NONE, LITTLE, vb.)
    
    private String parentFeedback; // Veli geribildirimi (opsiyonel)
    
    private LocalDateTime playedAt; // Oyunun oynandığı tarih (opsiyonel, gelmezse sunucu belirler)

    
    // --- Getter ve Setter'lar ---

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public int getGameDurationSeconds() {
        return gameDurationSeconds;
    }

    public void setGameDurationSeconds(int gameDurationSeconds) {
        this.gameDurationSeconds = gameDurationSeconds;
    }

    public int getMistakesMade() {
        return mistakesMade;
    }

    public void setMistakesMade(int mistakesMade) {
        this.mistakesMade = mistakesMade;
    }

    public boolean isParentHelped() {
        return parentHelped;
    }

    public void setParentHelped(boolean parentHelped) {
        this.parentHelped = parentHelped;
    }

    public HelpLevel getParentHelpLevel() {
        return parentHelpLevel;
    }

    public void setParentHelpLevel(HelpLevel parentHelpLevel) {
        this.parentHelpLevel = parentHelpLevel;
    }

    public String getParentFeedback() {
        return parentFeedback;
    }

    public void setParentFeedback(String parentFeedback) {
        this.parentFeedback = parentFeedback;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}