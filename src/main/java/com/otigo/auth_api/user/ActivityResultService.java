package com.otigo.auth_api.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityResultService {

    private final ActivityResultRepository activityResultRepository;
    private final ChildRepository childRepository; // Sonucun ekleneceği çocuğu bulmak için

    private final ActivityRepository activityRepository;

    public ActivityResultService(ActivityResultRepository activityResultRepository, ChildRepository childRepository, ActivityRepository activityRepository) {
        this.activityResultRepository = activityResultRepository;
        this.childRepository = childRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Mobil uygulamadan gelen oyun sonucunu veritabanına kaydeder.
     * @param childId Bu sonucun ait olduğu çocuğun ID'si
     * @param request Mobil uygulamadan gelen DTO (hata sayısı, skor vb.)
     * @return Veritabanına kaydedilen GameResult nesnesi
     */
    @Transactional // Bu metot veritabanına yazma işlemi yapar
    public ActivityResult saveActivityResult(Long childId, CreateActivityResultRequest request) {
        
        // 1. Sonucun ekleneceği çocuğu bul
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Oyun sonucu eklenecek çocuk bulunamadı. ID: " + childId));

        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new RuntimeException("Aktivite bulunamadı. ID: " + request.getActivityId()));        

        // 2. DTO'dan gelen verilerle yeni bir GameResult (Entity) nesnesi oluştur
        ActivityResult newResult = new ActivityResult();
        newResult.setChild(child); // Çocuğu bağla
        newResult.setActivity(activity);
        
        // DTO'daki tüm alanları Entity'ye kopyala
        //newResult.set(request.getGameName());
        newResult.setScore(request.getScore());
        newResult.setDurationSeconds(request.getDurationSeconds());
        newResult.setMistakesMade(request.getMistakesMade());
        newResult.setParentHelped(request.isParentHelped());
        newResult.setParentHelpLevel(request.getParentHelpLevel());
        newResult.setParentFeedback(request.getParentFeedback());
        
        // Eğer mobil uygulama tarih göndermediyse, şu anki zamanı kullan
        newResult.setPlayedAt(
            request.getPlayedAt() != null ? request.getPlayedAt() : LocalDateTime.now()
        );

        // 3. Yeni oluşturulan sonucu veritabanına kaydet
        return activityResultRepository.save(newResult);
    }

    /**
     * Bir çocuğa ait tüm oyun sonuçlarını (Gelişim Raporu için) listeler.
     * @param childId Raporu istenen çocuğun ID'si
     * @return O çocuğa ait, tarihe göre sıralanmış sonuç listesi
     */
    @Transactional(readOnly = true) // Sadece okuma işlemi
    public List<ActivityResult> getActivityResultsForChild(Long childId) {
        
        // 1. Çocuğu bul
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        
        // 2. GameResultRepository'de yazdığımız özel sorguyu çağır
        // (findByChildOrderByPlayedAtDesc)
        return activityResultRepository.findByChildOrderByPlayedAtDesc(child);
    }
}