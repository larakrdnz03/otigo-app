package com.otigo.auth_api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Güvenlik için
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.request.CreateActivityResultRequest;
import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.service.ActivityResultService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity-results") // Oyun sonuçları için yeni bir ana yol
public class ActivityResultController {

    private final ActivityResultService activityResultService;

    public ActivityResultController(ActivityResultService activityResultService) {
        this.activityResultService = activityResultService;
    }

    /**
     * Yeni bir aktivite sonucunu kaydeder.
     * Bu endpoint'i mobil uygulama, oyun bittiğinde çağırır.
     * POST /api/v1/activity-results/child/{childId}
     *
     * @param childId Sonucun ait olduğu çocuğun ID'si (URL'den gelir)
     * @param request Oyun sonucunun detaylarını içeren JSON verisi (Body'den gelir)
     */
    @PostMapping("/child/{childId}")
    public ResponseEntity<?> saveActivityResult(
            @PathVariable Long childId,
            @Valid @RequestBody CreateActivityResultRequest request,
            Authentication authentication) { // İsteği sadece giriş yapmış kullanıcıların (Veli/Uzman) yapabilmesi için
        
        // TODO: Ekstra Güvenlik Kontrolü: 
        // Giriş yapmış olan kullanıcının (authentication.getPrincipal()) 
        // bu 'childId'ye erişim yetkisi var mı? (O çocuğun velisi mi veya uzmanı mı?)
        // Şimdilik, giriş yapmış olmayı yeterli kabul ediyoruz.
        
        try {
            ActivityResult savedResult = activityResultService.saveActivityResult(childId, request);
            // Başarılı olursa 201 Created (Oluşturuldu) kodu ve
            // oluşturulan sonucun kendisini dön
            return ResponseEntity.status(HttpStatus.CREATED).body(savedResult);
        } catch (RuntimeException e) {
            // "Çocuk bulunamadı" hatasını yakala
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Bir çocuğa ait tüm oyun sonuçlarını (Gelişim Raporu) listeler.
     * Bu endpoint'i mobil uygulama, "Görsel Rapor" ekranını çizerken çağırır.
     * GET /api/v1/results/child/{childId}
     *
     * @param childId Raporu istenen çocuğun ID'si (URL'den gelir)
     */
    @GetMapping("/child/{childId}")
    public ResponseEntity<?> getActivityResultsForChild(
            @PathVariable Long childId,
            Authentication authentication) { // İsteği sadece giriş yapmış (Veli/Uzman) kullanıcıların yapabilmesi için

        // TODO: Ekstra Güvenlik Kontrolü (Yukarıdakiyle aynı)

        try {
            List<ActivityResult> results = activityResultService.getActivityResultsForChild(childId);
            // Başarılı olursa 200 OK ve sonuç listesini (JSON) dön
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            // "Çocuk bulunamadı" hatasını yakala
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}