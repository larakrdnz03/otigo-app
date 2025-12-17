package com.otigo.auth_api.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameResultService {

    private final GameResultRepository gameResultRepository;
    private final ChildRepository childRepository; // Sonucun ekleneceği çocuğu bulmak için

    public GameResultService(GameResultRepository gameResultRepository, ChildRepository childRepository) {
        this.gameResultRepository = gameResultRepository;
        this.childRepository = childRepository;
    }

    /**
     * Mobil uygulamadan gelen oyun sonucunu veritabanına kaydeder.
     * @param childId Bu sonucun ait olduğu çocuğun ID'si
     * @param request Mobil uygulamadan gelen DTO (hata sayısı, skor vb.)
     * @return Veritabanına kaydedilen GameResult nesnesi
     */
    @Transactional // Bu metot veritabanına yazma işlemi yapar
    public GameResult saveGameResult(Long childId, CreateGameResultRequest request) {
        
        // 1. Sonucun ekleneceği çocuğu bul
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Oyun sonucu eklenecek çocuk bulunamadı. ID: " + childId));

        // 2. DTO'dan gelen verilerle yeni bir GameResult (Entity) nesnesi oluştur
        GameResult newResult = new GameResult();
        newResult.setChild(child); // Çocuğu bağla
        
        // DTO'daki tüm alanları Entity'ye kopyala
        newResult.setGameName(request.getGameName());
        newResult.setScore(request.getScore());
        newResult.setGameDurationSeconds(request.getGameDurationSeconds());
        newResult.setMistakesMade(request.getMistakesMade());
        newResult.setParentHelped(request.isParentHelped());
        newResult.setParentHelpLevel(request.getParentHelpLevel());
        newResult.setParentFeedback(request.getParentFeedback());
        
        // Eğer mobil uygulama tarih göndermediyse, şu anki zamanı kullan
        newResult.setPlayedAt(
            request.getPlayedAt() != null ? request.getPlayedAt() : LocalDateTime.now()
        );

        // 3. Yeni oluşturulan sonucu veritabanına kaydet
        return gameResultRepository.save(newResult);
    }

    /**
     * Bir çocuğa ait tüm oyun sonuçlarını (Gelişim Raporu için) listeler.
     * @param childId Raporu istenen çocuğun ID'si
     * @return O çocuğa ait, tarihe göre sıralanmış sonuç listesi
     */
    @Transactional(readOnly = true) // Sadece okuma işlemi
    public List<GameResult> getGameResultsForChild(Long childId) {
        
        // 1. Çocuğu bul
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Rapor istenecek çocuk bulunamadı. ID: " + childId));
        
        // 2. GameResultRepository'de yazdığımız özel sorguyu çağır
        // (findByChildOrderByPlayedAtDesc)
        return gameResultRepository.findByChildOrderByPlayedAtDesc(child);
    }
}