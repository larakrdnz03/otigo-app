package com.otigo.auth_api.user;

import org.springframework.security.access.AccessDeniedException; // Güvenlik import'u
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SurveyService {

    private final SymptomSurveyRepository surveyRepository;
    private final ChildRepository childRepository;
    // UserRepository'ye (şimdilik) ihtiyacımız yok, Veli'yi (User)
    // Controller'dan (Authentication) alacağız.

    public SurveyService(SymptomSurveyRepository surveyRepository, ChildRepository childRepository) {
        this.surveyRepository = surveyRepository;
        this.childRepository = childRepository;
    }

    /**
     * Veliden gelen yeni anket sonucunu veritabanına kaydeder.
     * @param parentUser Anketi dolduran Veli (Giriş yapmış kullanıcı)
     * @param childId Anketin ait olduğu çocuğun ID'si
     * @param request Anketin JSON cevaplarını içeren DTO
     * @return Veritabanına kaydedilen SymptomSurvey nesnesi
     */
    @Transactional // Bu metot veritabanına yazma işlemi yapar
    public SymptomSurvey saveSurvey(UserEntity parentUser, Long childId, CreateSymptomSurveyRequest request) {

        // 1. Anketin ekleneceği çocuğu bul
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Anket eklenecek çocuk bulunamadı. ID: " + childId));

        // 2. ÖNEMLİ GÜVENLİK KONTROLÜ:
        // Anketi dolduran kişi, bu çocuğun velisi mi?
        // (Child.java'daki 'parent' alanını kontrol ediyoruz)
        if (!child.getParent().getId().equals(parentUser.getId())) {
            // Eğer Veli ID'leri eşleşmiyorsa, bu Veli'nin
            // başkasının çocuğu için anket doldurmasına izin verme.
            throw new AccessDeniedException("Bu çocuk için anket doldurma yetkiniz yok.");
        }

        // 3. DTO'dan gelen verilerle yeni bir SymptomSurvey (Entity) nesnesi oluştur
        SymptomSurvey newSurvey = new SymptomSurvey();
        newSurvey.setChild(child); // Çocuğu bağla
        newSurvey.setFilledBy(parentUser); // Veli'yi bağla
        newSurvey.setSurveyResultsJson(request.getSurveyResultsJson()); // JSON cevapları
        newSurvey.setSurveyDate(LocalDateTime.now()); // Anket tarihini şu an olarak ayarla

        // 4. Yeni oluşturulan anketi veritabanına kaydet
        return surveyRepository.save(newSurvey);
    }

    /**
     * Bir çocuğa ait tüm anket sonuçlarını (Uzmanın incelemesi için) listeler.
     * @param childId Raporu istenen çocuğun ID'si
     * @return O çocuğa ait, tarihe göre sıralanmış anket listesi
     */
    @Transactional(readOnly = true) // Sadece okuma işlemi
    public List<SymptomSurvey> getSurveysForChild(Long childId) {
        
        // 1. Çocuğu bul
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Anketleri istenecek çocuk bulunamadı. ID: " + childId));
        
        // 2. SymptomSurveyRepository'de yazdığımız özel sorguyu çağır
        // (findByChildOrderBySurveyDateDesc)
        return surveyRepository.findByChildOrderBySurveyDateDesc(child);
    }
}