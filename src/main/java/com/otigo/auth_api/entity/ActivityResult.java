package com.otigo.auth_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "activity_results")
public class ActivityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    @JsonIgnore
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    @JsonIgnore
    private Child child;

    //private Integer score;
    private int durationSeconds;
    private int mistakesMade;
    
    // SİLMEDİĞİMİZ ALAN:
    private boolean parentHelped;

    @Column(nullable = false)
    private int levelPlayed = 1;

    @Column(nullable = false)
    private int parentHelpCount = 0;

    @Column(nullable = false)
    private int totalTargetCount = 1;

    @Column(nullable = false)
    private double independenceScore = 100.0;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    // LEVEL DETAYLARI LİSTESİ
    @ElementCollection
    @CollectionTable(name = "level_results", joinColumns = @JoinColumn(name = "activity_result_id"))
    private List<LevelResult> levelResults = new ArrayList<>();

    public ActivityResult() {}

    // --- Getter ve Setter Metodları ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }

    //public Integer getScore() { return score; }
    //public void setScore(Integer score) { this.score = score; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public int getMistakesMade() { return mistakesMade; }
    public void setMistakesMade(int mistakesMade) { this.mistakesMade = mistakesMade; }

    // HATA ALDIĞIN KRİTİK METODLAR:
    public boolean isParentHelped() { return parentHelped; }
    public void setParentHelped(boolean parentHelped) { this.parentHelped = parentHelped; }

    public int getLevelPlayed() { return levelPlayed; }
    public void setLevelPlayed(int levelPlayed) { this.levelPlayed = levelPlayed; }

    public int getParentHelpCount() { return parentHelpCount; }
    public void setParentHelpCount(int parentHelpCount) { this.parentHelpCount = parentHelpCount; }

    public int getTotalTargetCount() { return totalTargetCount; }
    public void setTotalTargetCount(int totalTargetCount) { this.totalTargetCount = totalTargetCount; }

    public double getIndependenceScore() { return independenceScore; }
    public void setIndependenceScore(double independenceScore) { this.independenceScore = independenceScore; }

    public LocalDateTime getPlayedAt() { return playedAt; }
    public void setPlayedAt(LocalDateTime playedAt) { this.playedAt = playedAt; }

    public List<LevelResult> getLevelResults() { return levelResults; }
    public void setLevelResults(List<LevelResult> levelResults) { this.levelResults = levelResults; }
}