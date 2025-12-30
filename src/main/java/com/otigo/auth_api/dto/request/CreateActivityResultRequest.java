package com.otigo.auth_api.dto.request;

//import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import com.otigo.auth_api.entity.enums.HelpLevel;

/**
 * Bu bir DTO'dur. Mobil uygulamanın, bir oyun bittiğinde
 * backend'e göndereceği "Oyun Sonucu" verisini taşır.
 * * * DEĞİŞİKLİKLER:
 * 1. Sınıf adı GameResult -> ActivityResult oldu.
 * 2. gameName (String) yerine activityId (Long) kullanıldı.
 * 3. gameDurationSeconds -> durationSeconds oldu (Daha genel isim).
 */
public class CreateActivityResultRequest {

    @NotNull(message = "Aktivite ID boş olamaz")
    private Long activityId;

    @NotNull(message = "Skor boş olamaz")
    private Integer score; // Skor (Tamsayı)

    private int durationSeconds; //aktivite ne kadar sürdü
    
    private int mistakesMade; // Çocuk ne kadar hata yaptı
    
    private boolean parentHelped; // Veli yardım etti mi?
    
    private HelpLevel parentHelpLevel; // Ne kadar yardım etti? (NONE, LITTLE, vb.)
    
    private String parentFeedback; // Veli geribildirimi (opsiyonel)
    
    private LocalDateTime playedAt; // Oyunun oynandığı tarih (opsiyonel, gelmezse sunucu belirler)

    
    // --- Getter ve Setter'lar ---

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
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