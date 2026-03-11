package com.otigo.auth_api.dto.response;

import java.util.List;

public class GameReportDto {

    private String gameName;
    private Long activityId;
    private int currentLevel;
    private int totalSessionCount;
    private double avgMistakes;
    private double avgIndependenceScore;
    private double avgDurationSeconds;
    private List<GameSessionDto> sessions;

    public GameReportDto() {}

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public int getTotalSessionCount() { return totalSessionCount; }
    public void setTotalSessionCount(int totalSessionCount) { this.totalSessionCount = totalSessionCount; }

    public double getAvgMistakes() { return avgMistakes; }
    public void setAvgMistakes(double avgMistakes) { this.avgMistakes = avgMistakes; }

    public double getAvgIndependenceScore() { return avgIndependenceScore; }
    public void setAvgIndependenceScore(double avgIndependenceScore) { this.avgIndependenceScore = avgIndependenceScore; }

    public double getAvgDurationSeconds() { return avgDurationSeconds; }
    public void setAvgDurationSeconds(double avgDurationSeconds) { this.avgDurationSeconds = avgDurationSeconds; }

    public List<GameSessionDto> getSessions() { return sessions; }
    public void setSessions(List<GameSessionDto> sessions) { this.sessions = sessions; }
}