package com.otigo.auth_api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private final ActivityRepository activityRepository;
    private final ActivityResultRepository activityResultRepository; // Sonuçları kaydetmek için lazım

    // Constructor Injection (En sağlıklı yöntem)
    public ActivityService(ActivityRepository activityRepository, 
                           ActivityResultRepository activityResultRepository) {
        this.activityRepository = activityRepository;
        this.activityResultRepository = activityResultRepository;
    }


    // oyunlar
    private final List<String> GAME_NAMES = Arrays.asList(
        "Gölge-Nesne Eşleştirme",
        "Farklı Cisim Bulma",
        "Doğru Nesneyi Seçme",
        "Labirent Takibi",
        "Renk Boyama",
        "Sayı-Nesne Eşleştirme",
        "Yapboz",
        "Kelime Düzeltme",
        "Zıt Kavramlar"
    );

    private final List<String> EVENT_NAMES = Arrays.asList("Hikaye Dinleyip Soru Cevaplama");


    /**
     * Yeni bir çocuk kaydolduğunda bu metodu çağıracağız.
     * Çocuğa tüm oyunları 1. seviyeden başlatarak oluşturur.
     */
    @Transactional
    public void createInitialActivitiesForChild(Child child) {
        for (String name : GAME_NAMES) {
            /*Activity game = new Activity(gameName, ActivityType.OYUN, child);
            activityRepository.save(game);*/
            // Eğer bu çocukta bu oyun zaten varsa tekrar ekleme (Kontrolü)
            if (activityRepository.findByChildIdAndName(child.getId(), name).isEmpty()) {
                Activity game = new Activity(name, ActivityType.OYUN, child);
                activityRepository.save(game);
            }
        }

        // 2. Etkinlikleri Kaydet (Tip: EVENT)
        /*for (String eventName : DEFAULT_EVENTS) {
            Activity event = new Activity(eventName, ActivityType.ETKINLIK, child);
            activityRepository.save(event);
        }*/
        for (String name : EVENT_NAMES) {
             if (activityRepository.findByChildIdAndName(child.getId(), name).isEmpty()) {
                Activity event = new Activity(name, ActivityType.ETKINLIK, child);
                activityRepository.save(event);
            }
        }
    }

    /**
     * Çocuğun sadece oyun listesini getirir.
     */
    public List<Activity> getGamesForChild(Long childId) {
        return activityRepository.findByChildIdAndType(childId, ActivityType.OYUN);
    }

    // Sadece Etkinlikleri Getir
    public List<Activity> getEventsForChild(Long childId) {
        return activityRepository.findByChildIdAndType(childId, ActivityType.ETKINLIK);
    }

    @Transactional
    public void saveActivityResult(Long childId, CreateActivityResultRequest request) {
        
        // 1. Aktiviteyi bul (ID ile)
        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new RuntimeException("Aktivite bulunamadı! ID: " + request.getActivityId()));

        // 2. Güvenlik Kontrolü: Bu aktivite gerçekten bu çocuğa mı ait?
        if (!activity.getChild().getId().equals(childId)) {
            throw new RuntimeException("Hata: Bu aktivite belirtilen çocuğa ait değil!");
        }

        // 3. Entity Oluştur (Result)
        ActivityResult result = new ActivityResult();
        result.setActivity(activity);
        result.setChild(activity.getChild()); // İlişkiyi kuruyoruz
        result.setScore(request.getScore());
        result.setDurationSeconds(request.getDurationSeconds());
        result.setMistakesMade(request.getMistakesMade());
        result.setParentHelped(request.isParentHelped());
        result.setParentHelpLevel(request.getParentHelpLevel());
        result.setParentFeedback(request.getParentFeedback());

        // Tarih kontrolü (Mobil göndermediyse şu anı al)
        result.setPlayedAt(request.getPlayedAt() != null ? request.getPlayedAt() : LocalDateTime.now());

        // 4. Sonucu Kaydet
        activityResultRepository.save(result);

        // 5. Aktivitenin "Son Oynanma Tarihi"ni güncelle
        activity.setLastPlayedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }

    // Çocuk oyunu bitirince bu metodu çağır
    public void updateLastPlayedTime(Long activityId) {
       Activity activity = activityRepository.findById(activityId).orElseThrow();
       activity.setLastPlayedAt(LocalDateTime.now()); // <-- SAATİ GÜNCELLE
       activityRepository.save(activity);
    }
}