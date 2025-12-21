package com.otigo.auth_api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/activities") // Ortak adres
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    // 1. GET: Çocuğun Oyunlarını Listele
    // Örn: http://localhost:8080/api/activities/child/5/games
    @GetMapping("/child/{childId}/games")
    public ResponseEntity<List<Activity>> getChildGames(@PathVariable Long childId) {
        List<Activity> games = activityService.getGamesForChild(childId);
        return ResponseEntity.ok(games);
    }

    // 2. GET: Çocuğun Etkinliklerini Listele
    // Örn: http://localhost:8080/api/activities/child/5/events
    @GetMapping("/child/{childId}/events")
    public ResponseEntity<List<Activity>> getChildEvents(@PathVariable Long childId) {
        List<Activity> events = activityService.getEventsForChild(childId);
        return ResponseEntity.ok(events);
    }

    // 3. POST: Oyun/Etkinlik Sonucu Kaydet
    // Örn: http://localhost:8080/api/activities/child/5/result
    /*@PostMapping("/child/{childId}/result")
    public ResponseEntity<String> saveActivityResult(
            @PathVariable Long childId,
            @RequestBody CreateActivityResultRequest request) {
        
        try {
            activityService.saveActivityResult(childId, request);
            return ResponseEntity.ok("Aktivite sonucu başarıyla kaydedildi.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }*/
}