package com.otigo.auth_api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import com.otigo.auth_api.entity.enums.HelpLevel;

/**
 * Mobil uygulamanın (Unity -> Android -> Backend) bir oyun bittiğinde
 * backend'e göndereceği "Oyun Sonucu" verisini taşır.
 *
 * DEĞİŞİKLİKLER (Görsel Algı Raporu için):
 * - parentHelpCount  : Veli kaç kez yardım etti (eski: sadece true/false vardı)
 * - totalTargetCount : O level'daki toplam nesne/seçim sayısı (bağımsızlık formülü için)
 *
 * Bağımsızlık Formülü:
 * bagimsizlik (%) = (1 - parentHelpCount / totalTargetCount) x 100
 */
public class CreateActivityResultRequest {

    @NotNull(message = "Aktivite ID boş olamaz")
    private Long activityId;

    @NotNull(message = "Skor boş olamaz")
    private Integer score;

    private int durationSeconds;

    private int mistakesMade;

    // --- ESKİ ALAN (geriye dönük uyumluluk için bırakıldı) ---
    private boolean parentHelped;

    private HelpLevel parentHelpLevel;

    private String parentFeedback;

    private LocalDateTime playedAt;

    /**
     * Bu oyunun oynandığı andaki seviye.
     * Unity tarafından gönderilir.
     * Örn: Çocuk 3. level'daysa levelPlayed = 3
     */
    private int levelPlayed = 1;

    // --- YENİ ALANLAR ---

    /**
     * Veli kaç kez yardım etti?
     * Veli uygulamadaki "Yardım Ettim" butonuna her bastığında bu sayı 1 artar.
     * Örnek: Level'da 4 nesne var, veli 2 tanesinde yardım etti → parentHelpCount = 2
     */
    private int parentHelpCount = 0;

    /**
     * O level'daki toplam hedef sayısı.
     * Gölge-Nesne: o level'daki nesne sayısı (Level 6 → 4)
     * Farklı Cisim: beklenen doğru seçim sayısı (tek cevap → 1, çoklu → 2+)
     * Doğru Nesneyi Seçme: beklenen doğru seçim sayısı
     *
     * Bu değer Unity tarafından gönderilir.
     */
    private int totalTargetCount = 1;

    // --- Getter ve Setter'lar ---

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public int getMistakesMade() { return mistakesMade; }
    public void setMistakesMade(int mistakesMade) { this.mistakesMade = mistakesMade; }

    public boolean isParentHelped() { return parentHelped; }
    public void setParentHelped(boolean parentHelped) { this.parentHelped = parentHelped; }

    public HelpLevel getParentHelpLevel() { return parentHelpLevel; }
    public void setParentHelpLevel(HelpLevel parentHelpLevel) { this.parentHelpLevel = parentHelpLevel; }

    public String getParentFeedback() { return parentFeedback; }
    public void setParentFeedback(String parentFeedback) { this.parentFeedback = parentFeedback; }

    public LocalDateTime getPlayedAt() { return playedAt; }
    public void setPlayedAt(LocalDateTime playedAt) { this.playedAt = playedAt; }

    public int getLevelPlayed() { return levelPlayed; }
    public void setLevelPlayed(int levelPlayed) { this.levelPlayed = levelPlayed; }

    public int getParentHelpCount() { return parentHelpCount; }
    public void setParentHelpCount(int parentHelpCount) { this.parentHelpCount = parentHelpCount; }

    public int getTotalTargetCount() { return totalTargetCount; }
    public void setTotalTargetCount(int totalTargetCount) { this.totalTargetCount = totalTargetCount; }
}