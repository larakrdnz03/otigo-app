package com.otigo.auth_api.dto.response;

public class GameSessionDto {

    private int sessionNumber;
    private int level;
    private int mistakesMade;
    private int parentHelpCount;
    private int totalTargetCount;
    private double independenceScore;
    private int durationSeconds;
    private int retryCount;

    public GameSessionDto() {}

    public static GameSessionDto from(
            int sessionNumber,
            int level,
            int mistakesMade,
            int parentHelpCount,
            int totalTargetCount,
            double independenceScore,
            int durationSeconds) {
        GameSessionDto dto = new GameSessionDto();
        dto.sessionNumber = sessionNumber;
        dto.level = level;
        dto.mistakesMade = mistakesMade;
        dto.parentHelpCount = parentHelpCount;
        dto.totalTargetCount = totalTargetCount;
        dto.independenceScore = independenceScore;
        dto.durationSeconds = durationSeconds;
        return dto;
    }

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

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}