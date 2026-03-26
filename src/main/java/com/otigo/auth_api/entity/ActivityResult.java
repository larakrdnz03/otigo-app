package com.otigo.auth_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otigo.auth_api.entity.enums.HelpLevel;
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

    private Integer score;
    private int durationSeconds;
    private int mistakesMade;
    private boolean parentHelped;

    /*@Enumerated(EnumType.STRING)
    private HelpLevel parentHelpLevel;*/

    /*@Lob
    @Column(columnDefinition = "TEXT")
    private String parentFeedback;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    @JsonIgnore
    private Child child;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    @Column(nullable = false)
    private int levelPlayed = 1;

    @Column(nullable = false)
    private int parentHelpCount = 0;

    @Column(nullable = false)
    private int totalTargetCount = 1;

    @Column(nullable = false)
    private double independenceScore = 100.0;

    public ActivityResult() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public int getMistakesMade() { return mistakesMade; }
    public void setMistakesMade(int mistakesMade) { this.mistakesMade = mistakesMade; }

    public boolean isParentHelped() { return parentHelped; }
    public void setParentHelped(boolean parentHelped) { this.parentHelped = parentHelped; }

    /*public HelpLevel getParentHelpLevel() { return parentHelpLevel; }
    public void setParentHelpLevel(HelpLevel parentHelpLevel) { this.parentHelpLevel = parentHelpLevel; }*/

    /*public String getParentFeedback() { return parentFeedback; }
    public void setParentFeedback(String parentFeedback) { this.parentFeedback = parentFeedback; }*/

    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }

    public LocalDateTime getPlayedAt() { return playedAt; }
    public void setPlayedAt(LocalDateTime playedAt) { this.playedAt = playedAt; }

    public int getLevelPlayed() { return levelPlayed; }
    public void setLevelPlayed(int levelPlayed) { this.levelPlayed = levelPlayed; }

    public int getParentHelpCount() { return parentHelpCount; }
    public void setParentHelpCount(int parentHelpCount) { this.parentHelpCount = parentHelpCount; }

    public int getTotalTargetCount() { return totalTargetCount; }
    public void setTotalTargetCount(int totalTargetCount) { this.totalTargetCount = totalTargetCount; }

    public double getIndependenceScore() { return independenceScore; }
    public void setIndependenceScore(double independenceScore) { this.independenceScore = independenceScore; }
}
