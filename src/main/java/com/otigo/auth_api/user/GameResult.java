package com.otigo.auth_api.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_results")
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String gameName; // Örn: "Renk Eşleştirme"

    private int score; // Skor (Tamsayı)

    private int gameDurationSeconds; // Oyun ne kadar sürdü (saniye)

    private int mistakesMade; // Çocuk ne kadar hata yaptı

    private boolean parentHelped; // Veli yardım etti mi? (True/False)

    @Enumerated(EnumType.STRING) // Enum'u metin olarak kaydet (örn: "LITTLE")
    private HelpLevel parentHelpLevel; // Ne kadar yardım etti?

    @Lob // Uzun metin alanı
    @Column(columnDefinition = "TEXT")
    private String parentFeedback; // Veli geribildirim/not ekledi mi?
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child; // Bu sonucun ait olduğu çocuk

    @Column(nullable = false)
    private LocalDateTime playedAt; // Oyunun oynandığı tarih

    // --- Constructor'lar ---

    public GameResult() {
        // JPA için boş constructor
    }

    // --- Getter ve Setter'lar ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
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

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}