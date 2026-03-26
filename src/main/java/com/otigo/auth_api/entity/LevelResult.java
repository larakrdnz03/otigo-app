package com.otigo.auth_api.entity;

import jakarta.persistence.Embeddable;

@Embeddable // Ayrı bir tablo yerine ana tabloyla ilişkili bir alt küme oluşturur
public class LevelResult {

    private int levelNumber;      // Kaçıncı seviye? (1, 2, 3...)
    private int durationSeconds;  // Sadece bu seviyede geçen süre
    private int mistakesMade;     // Sadece bu seviyede yapılan hata
    private int helpCount;        // Sadece bu seviyedeki yardım sayısı

    public LevelResult() {}

    public LevelResult(int levelNumber, int durationSeconds, int mistakesMade, int helpCount) {
        this.levelNumber = levelNumber;
        this.durationSeconds = durationSeconds;
        this.mistakesMade = mistakesMade;
        this.helpCount = helpCount;
    }

    // --- Getter ve Setterlar ---
    public int getLevelNumber() { return levelNumber; }
    public void setLevelNumber(int levelNumber) { this.levelNumber = levelNumber; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public int getMistakesMade() { return mistakesMade; }
    public void setMistakesMade(int mistakesMade) { this.mistakesMade = mistakesMade; }

    public int getHelpCount() { return helpCount; }
    public void setHelpCount(int helpCount) { this.helpCount = helpCount; }
}