package com.otigo.auth_api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.request.CreateSymptomSurveyRequest;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.ExpertParentConnection;
import com.otigo.auth_api.entity.ExpertParentConnection.ConnectionStatus;
import com.otigo.auth_api.entity.SymptomSurvey;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.UserRole;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.ExpertParentConnectionRepository;
import com.otigo.auth_api.service.SurveyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/surveys")
public class SurveyController {

    private final SurveyService surveyService;
    private final ChildRepository childRepository;
    private final ExpertParentConnectionRepository connectionRepository;

    public SurveyController(SurveyService surveyService,
                            ChildRepository childRepository,
                            ExpertParentConnectionRepository connectionRepository) {
        this.surveyService = surveyService;
        this.childRepository = childRepository;
        this.connectionRepository = connectionRepository;
    }

    /**
     * Veli tarafından doldurulan yeni bir belirti anketini kaydeder.
     */
    @PostMapping("/child/{childId}")
    public ResponseEntity<?> saveSymptomSurvey(
            @PathVariable Long childId,
            @Valid @RequestBody CreateSymptomSurveyRequest request,
            Authentication authentication) {

        try {
            UserEntity parentUser = (UserEntity) authentication.getPrincipal();
            SymptomSurvey savedSurvey = surveyService.saveSurvey(parentUser, childId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSurvey);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Bir çocuğa ait tüm anket sonuçlarını listeler.
     * Veli kendi çocuğuna, uzman bağlı olduğu velinin çocuğuna erişebilir.
     */
    @GetMapping("/child/{childId}")
    public ResponseEntity<?> getSurveysForChild(
            @PathVariable Long childId,
            Authentication authentication) {

        try {
            UserEntity currentUser = (UserEntity) authentication.getPrincipal();

            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));

            // Veli ise kendi çocuğu mu kontrol et
            if (currentUser.getRole() == UserRole.VELI) {
                if (!child.getParent().getId().equals(currentUser.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Bu çocuğun anketlerine erişim yetkiniz yok.");
                }
            }

            // Uzman ise bağlı olduğu velinin çocuğu mu kontrol et
            if (currentUser.getRole() == UserRole.UZMAN) {
                UserEntity parent = child.getParent();
                boolean isConnected = connectionRepository
                        .findByExpertAndParent(currentUser, parent)
                        .map(c -> c.getStatus() == ConnectionStatus.ACCEPTED)
                        .orElse(false);

                if (!isConnected) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Bu çocuğun anketlerine erişim yetkiniz yok.");
                }
            }

            List<SymptomSurvey> surveys = surveyService.getSurveysForChild(childId);
            return ResponseEntity.ok(surveys);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}