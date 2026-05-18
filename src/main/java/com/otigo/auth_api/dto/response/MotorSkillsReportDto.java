package com.otigo.auth_api.dto.response;

/**
 * "Motor Becerileri ve Planlama" kategorisinin tam raporunu taşır.
 *
 * Bu kategori 2 oyun içerir:
 * 1. Labirent Takibi - duvara çarpma veya yanlış hedefe gitme hata sayılır
 * 2. Renk Boyama - yanlış renge basma veya yanlış hedefe renk götürme hata sayılır
 *
 * Endpoint: GET /api/reports/{childId}/motor-skills
 */
public class MotorSkillsReportDto {

    private Long childId;
    private String childName;
    private GameReportDto mazeTracking;
    private GameReportDto colorPainting;

    public MotorSkillsReportDto() {}

    public Long getChildId() { return childId; }
    public void setChildId(Long childId) { this.childId = childId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public GameReportDto getMazeTracking() { return mazeTracking; }
    public void setMazeTracking(GameReportDto mazeTracking) { this.mazeTracking = mazeTracking; }

    public GameReportDto getColorPainting() { return colorPainting; }
    public void setColorPainting(GameReportDto colorPainting) { this.colorPainting = colorPainting; }
}