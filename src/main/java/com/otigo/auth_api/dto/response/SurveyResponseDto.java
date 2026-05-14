package com.otigo.auth_api.dto.response;

import com.otigo.auth_api.entity.SymptomSurvey;
import java.time.LocalDateTime;

public class SurveyResponseDto {

    private Long id;
    private String surveyResultsJson;
    private LocalDateTime surveyDate;

    public static SurveyResponseDto from(SymptomSurvey survey) {
        SurveyResponseDto dto = new SurveyResponseDto();
        dto.setId(survey.getId());
        dto.setSurveyResultsJson(survey.getSurveyResultsJson());
        dto.setSurveyDate(survey.getSurveyDate());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSurveyResultsJson() { return surveyResultsJson; }
    public void setSurveyResultsJson(String surveyResultsJson) { this.surveyResultsJson = surveyResultsJson; }

    public LocalDateTime getSurveyDate() { return surveyDate; }
    public void setSurveyDate(LocalDateTime surveyDate) { this.surveyDate = surveyDate; }
}