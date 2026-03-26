package com.otigo.auth_api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.request.CreateActivityResultRequest;
import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.service.ActivityResultService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities") // Daha kısa ve standart bir yol
public class ActivityResultController {

    private final ActivityResultService activityResultService;

    public ActivityResultController(ActivityResultService activityResultService) {
        this.activityResultService = activityResultService;
    }

    /**
     * POST /api/v1/activities/result/{childId}
     * Unity bu endpoint'i kullanarak sonucu gönderir.
     */
    @PostMapping("/result/{childId}")
    public ResponseEntity<?> saveActivityResult(
            @PathVariable Long childId,
            @Valid @RequestBody CreateActivityResultRequest request,
            Authentication authentication) {
        
        try {
            // Service katmanında hem ana sonucu hem de içindeki levelResults listesini kaydediyoruz
            ActivityResult savedResult = activityResultService.saveActivityResult(childId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedResult);
        } catch (Exception e) {
            // Hata durumunda arkadaşına anlamlı bir mesaj dönelim
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Sonuç kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    /**
     * GET /api/v1/activities/reports/{childId}
     * Veli ekranında raporları listelerken kullanılır.
     */
    @GetMapping("/reports/{childId}")
    public ResponseEntity<?> getActivityResultsForChild(@PathVariable Long childId) {
        try {
            List<ActivityResult> results = activityResultService.getActivityResultsForChild(childId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Raporlar getirilemedi: " + e.getMessage());
        }
    }
}