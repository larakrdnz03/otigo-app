package com.otigo.auth_api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import com.otigo.auth_api.entity.LevelResult;
import com.otigo.auth_api.entity.enums.HelpLevel;

public class CreateActivityResultRequest {

    @NotNull(message = "Aktivite ID boş olamaz")
    private Long activityId;

    @NotNull(message = "Skor boş olamaz")
    private Integer score;

    private int durationSeconds;
    private int mistakesMade;
    private boolean parentHelped;
    private HelpLevel parentHelpLevel;
    private String parentFeedback;
    private LocalDateTime playedAt;
    private int levelPlayed = 1;
    private int parentHelpCount = 0;
    private int totalTargetCount = 1;
    private List<LevelResult> levelResults = new ArrayList<>();

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

    public List<LevelResult> getLevelResults() { return levelResults; }
    public void setLevelResults(List<LevelResult> levelResults) { this.levelResults = levelResults; }
}