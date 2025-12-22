package com.otigo.auth_api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.otigo.auth_api.dto.request.CreateObservationRequest;
import com.otigo.auth_api.dto.request.CreateRecommendationRequest;
import com.otigo.auth_api.dto.response.ReportResponse;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.ExpertRecommendation;
import com.otigo.auth_api.entity.Observation;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.UserRepository;
import com.otigo.auth_api.service.ExpertService;
import com.otigo.auth_api.service.ReportService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/expert")
public class ExpertController {

    private final ExpertService expertService;
    private final UserRepository userRepository;
    
    // --- YENİ EKLENDİ ---
    private final ReportService reportService; // Rapor Servisini enjekte et

    // --- CONSTRUCTOR GÜNCELLENDİ ---
    public ExpertController(ExpertService expertService, 
                            UserRepository userRepository,
                            ReportService reportService) { // YENİ PARAMETRE
        this.expertService = expertService;
        this.userRepository = userRepository;
        this.reportService = reportService; // YENİ ATAMA
    }

    // ... (getTrackedChildren, trackChild, addObservation, getObservationsForChild metotları) ...
    // ... (Bu metotlarda bir değişiklik yok, oldukları gibi kalıyorlar) ...
    
    @GetMapping("/children")
    public ResponseEntity<Set<Child>> getMyTrackedChildren(Authentication authentication) {
        UserEntity expertUser = (UserEntity) authentication.getPrincipal();
        UserEntity freshExpert = userRepository.findById(expertUser.getId()).get();
        Set<Child> children = expertService.getTrackedChildren(freshExpert);
        return ResponseEntity.ok(children);
    }

    @PostMapping("/track/{childId}")
    public ResponseEntity<String> trackChild(
            Authentication authentication,
            @PathVariable Long childId) {
        try {
            UserEntity expertUser = (UserEntity) authentication.getPrincipal();
            expertService.trackChild(expertUser, childId);
            return ResponseEntity.ok("Çocuk başarıyla takip listesine eklendi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/observations/{childId}")
    public ResponseEntity<?> addObservation(
            Authentication authentication,
            @PathVariable Long childId,
            @Valid @RequestBody CreateObservationRequest request) {
        try {
            UserEntity expertUser = (UserEntity) authentication.getPrincipal();
            Observation newObservation = (Observation) expertService.addObservation(expertUser, childId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newObservation);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/observations/{childId}")
    public ResponseEntity<?> getObservationsForChild(
            Authentication authentication,
            @PathVariable Long childId) {
        try {
            List<com.otigo.auth_api.entity.Observation> observations = expertService.getObservationsForChild(childId);
            return ResponseEntity.ok(observations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/recommendations/{childId}")
    public ResponseEntity<?> addRecommendation(
            Authentication authentication,
            @PathVariable Long childId,
            @Valid @RequestBody CreateRecommendationRequest request) {
        try {
            UserEntity expertUser = (UserEntity) authentication.getPrincipal();
            ExpertRecommendation newRecommendation = expertService.addRecommendation(expertUser, childId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newRecommendation);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/recommendations/{childId}")
    public ResponseEntity<?> getRecommendationsForChild(
            Authentication authentication,
            @PathVariable Long childId) {
        try {
            List<ExpertRecommendation> recommendations = expertService.getRecommendationsForChild(childId);
            return ResponseEntity.ok(recommendations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // --- YENİ EKLENEN ENDPOINT: TAM GELİŞİM RAPORU ---
    /**
     * Bir çocuk için "tam ve analiz edilmiş" gelişim raporunu getirir.
     * Bu, mobil uygulamanın tüm grafikleri ve listeleri çizmek için
     * çağıracağı ana endpoint'tir.
     * GET /api/v1/expert/report/{childId}
     *
     * @param childId Raporu istenen çocuğun ID'si
     */
    @GetMapping("/report/{childId}")
    public ResponseEntity<?> getComprehensiveReportForChild(
            @PathVariable Long childId,
            Authentication authentication) { // İsteğin güvenli olduğunu garanti eder

        // TODO: Ekstra Güvenlik Kontrolü: 
        // Giriş yapmış olan kullanıcının (authentication.getPrincipal()) 
        // bu 'childId'ye erişim yetkisi var mı? (O çocuğun velisi mi veya uzmanı mı?)
        
        try {
            // 1. "Aşçı" (ReportService) çağır ve "pişmiş keki" (ReportResponse) al
            ReportResponse reportData = reportService.generateReport(childId);
            
            // 2. Analiz edilmiş tam raporu (JSON) mobil uygulamaya dön
            return ResponseEntity.ok(reportData);
            
        } catch (RuntimeException e) {
            // "Çocuk bulunamadı" hatasını yakala
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}