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
import com.otigo.auth_api.service.NotificationService;
import com.otigo.auth_api.service.SurveyService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/surveys")
public class SurveyController {

    private final SurveyService surveyService;
    private final ChildRepository childRepository;
    private final ExpertParentConnectionRepository connectionRepository;
    private final NotificationService notificationService;

    public SurveyController(SurveyService surveyService,
                            ChildRepository childRepository,
                            ExpertParentConnectionRepository connectionRepository,
                            NotificationService notificationService) {
        this.surveyService = surveyService;
        this.childRepository = childRepository;
        this.connectionRepository = connectionRepository;
        this.notificationService = notificationService;
    }

    /**
     * Veli yeni anket kaydeder.
     * POST /api/v1/surveys/child/{childId}
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
     * Veli mevcut anketi günceller, bağlı uzmana bildirim gider.
     * PUT /api/v1/surveys/{surveyId}
     * Body: { "surveyResultsJson": "..." }
     */
    @PutMapping("/{surveyId}")
    public ResponseEntity<?> updateSurvey(
            @PathVariable Long surveyId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        try {
            UserEntity parentUser = (UserEntity) authentication.getPrincipal();
            String newJson = body.get("surveyResultsJson");

            SymptomSurvey updated = surveyService.updateSurvey(parentUser, surveyId, newJson);

            // Bağlı uzmanlara bildirim gönder
            Child child = updated.getChild();
            List<ExpertParentConnection> connections = connectionRepository
                    .findByParentAndStatus(parentUser, ConnectionStatus.ACCEPTED);

            String parentName = parentUser.getFirstname() != null ? parentUser.getFirstname() : "Veli";
            for (ExpertParentConnection conn : connections) {
                notificationService.sendNotification(
                        conn.getExpert(),
                        "Belirti Formu Güncellendi",
                        parentName + ", " + child.getName() + " için belirti formunu güncelledi."
                );
            }

            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Çocuğun anketlerini listeler.
     * GET /api/v1/surveys/child/{childId}
     */
    @GetMapping("/child/{childId}")
    public ResponseEntity<?> getSurveysForChild(
            @PathVariable Long childId,
            Authentication authentication) {

        try {
            UserEntity currentUser = (UserEntity) authentication.getPrincipal();

            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));

            if (currentUser.getRole() == UserRole.VELI) {
                if (!child.getParent().getId().equals(currentUser.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Bu çocuğun anketlerine erişim yetkiniz yok.");
                }
            }

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