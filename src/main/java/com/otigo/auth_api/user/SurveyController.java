package com.otigo.auth_api.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// NOT: UserEntity aynı pakette olduğu için 'import' satırına gerek yoktur.
// Otomatik olarak tanıyacaktır.

@RestController
@RequestMapping("/api/v1/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
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
            // 1. Kritik Nokta: Authentication'dan gelen nesneyi UserEntity'ye çeviriyoruz (Cast)
            UserEntity parentUser = (UserEntity) authentication.getPrincipal();

            // 2. Servise bu UserEntity nesnesini gönderiyoruz
            // (SurveyService'teki saveSurvey metodunun da UserEntity beklediğinden emin ol!)
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
     */
    @GetMapping("/child/{childId}")
    public ResponseEntity<?> getSurveysForChild(
            @PathVariable Long childId,
            Authentication authentication) { 

        try {
            List<SymptomSurvey> surveys = surveyService.getSurveysForChild(childId);
            return ResponseEntity.ok(surveys);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}