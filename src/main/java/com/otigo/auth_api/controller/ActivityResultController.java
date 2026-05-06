package com.otigo.auth_api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.request.CreateActivityResultRequest;
import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.service.ActivityResultService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityResultController {

    private final ActivityResultService activityResultService;
    private final ChildRepository childRepository;

    public ActivityResultController(ActivityResultService activityResultService,
                                    ChildRepository childRepository) {
        this.activityResultService = activityResultService;
        this.childRepository = childRepository;
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
            UserEntity currentUser = (UserEntity) authentication.getPrincipal();

            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));

            if (!child.getParent().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Bu çocuk için sonuç kaydetme yetkiniz yok.");
            }

            ActivityResult savedResult = activityResultService.saveActivityResult(childId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedResult);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Sonuç kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    /**
     * GET /api/v1/activities/reports/{childId}
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