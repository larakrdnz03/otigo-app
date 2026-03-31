package com.otigo.auth_api.dto.response;

import java.util.List;

/**
 * Aylık gelişim trendi verisi.
 * Endpoint: GET /api/reports/{childId}/monthly-trend
 */
public class MonthlyTrendDto {

    private Long childId;
    private String childName;
    private List<String> months;
    private List<Double> visualPerception;
    private List<Double> motorSkills;
    private List<Double> mathSkills;
    private List<Double> languageSkills;

    public MonthlyTrendDto() {}

    public Long getChildId() { return childId; }
    public void setChildId(Long childId) { this.childId = childId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public List<String> getMonths() { return months; }
    public void setMonths(List<String> months) { this.months = months; }

    public List<Double> getVisualPerception() { return visualPerception; }
    public void setVisualPerception(List<Double> visualPerception) { this.visualPerception = visualPerception; }

    public List<Double> getMotorSkills() { return motorSkills; }
    public void setMotorSkills(List<Double> motorSkills) { this.motorSkills = motorSkills; }

    public List<Double> getMathSkills() { return mathSkills; }
    public void setMathSkills(List<Double> mathSkills) { this.mathSkills = mathSkills; }

    public List<Double> getLanguageSkills() { return languageSkills; }
    public void setLanguageSkills(List<Double> languageSkills) { this.languageSkills = languageSkills; }
}