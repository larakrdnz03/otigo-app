package com.otigo.auth_api.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.dto.request.CreateSymptomSurveyRequest;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.SymptomSurvey;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.SymptomSurveyRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SurveyService {

    private final SymptomSurveyRepository surveyRepository;
    private final ChildRepository childRepository;

    public SurveyService(SymptomSurveyRepository surveyRepository, ChildRepository childRepository) {
        this.surveyRepository = surveyRepository;
        this.childRepository = childRepository;
    }

    @Transactional
    public SymptomSurvey saveSurvey(UserEntity parentUser, Long childId, CreateSymptomSurveyRequest request) {

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Anket eklenecek çocuk bulunamadı. ID: " + childId));

        if (!child.getParent().getId().equals(parentUser.getId())) {
            throw new AccessDeniedException("Bu çocuk için anket doldurma yetkiniz yok.");
        }

        SymptomSurvey newSurvey = new SymptomSurvey();
        newSurvey.setChild(child);
        newSurvey.setParent(parentUser);
        newSurvey.setSurveyResultsJson(request.getSurveyResultsJson());
        newSurvey.setSurveyDate(
            request.getSurveyDate() != null ? request.getSurveyDate() : LocalDateTime.now()
        );

        return surveyRepository.save(newSurvey);
    }

    @Transactional
    public SymptomSurvey updateSurvey(UserEntity parentUser, Long surveyId, String newSurveyResultsJson) {

        SymptomSurvey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Anket bulunamadı. ID: " + surveyId));

        if (!survey.getParent().getId().equals(parentUser.getId())) {
            throw new AccessDeniedException("Bu anketi düzenleme yetkiniz yok.");
        }

        survey.setSurveyResultsJson(newSurveyResultsJson);
        survey.setSurveyDate(LocalDateTime.now());

        return surveyRepository.save(survey);
    }

    @Transactional(readOnly = true)
    public List<SymptomSurvey> getSurveysForChild(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Anketleri istenecek çocuk bulunamadı. ID: " + childId));
        return surveyRepository.findByChildOrderBySurveyDateDesc(child);
    }

    @Transactional(readOnly = true)
    public SymptomSurvey getSurveyById(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Anket bulunamadı. ID: " + surveyId));
    }
}