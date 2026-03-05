package com.otigo.auth_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.dto.response.GameReportDto;
import com.otigo.auth_api.dto.response.GameSessionDto;
import com.otigo.auth_api.dto.response.VisualPerceptionReportDto;
import com.otigo.auth_api.entity.Activity;
import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.repository.ActivityRepository;
import com.otigo.auth_api.repository.ActivityResultRepository;
import com.otigo.auth_api.repository.ChildRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * "Görsel Algı ve Dikkat Becerileri" kategorisinin raporunu üretir.
 *
 * Kategori oyunları:
 * - Gölge-Nesne Eşleştirme  (GAME_SHADOW_MATCH)
 * - Farklı Cisim Bulma       (GAME_FIND_DIFFERENT)
 * - Doğru Nesneyi Seçme      (GAME_SELECT_CORRECT)
 */
@Service
public class VisualPerceptionReportService {

    // Oyun isimleri ActivityService ile tutarlı olmalı
    private static final String SHADOW_MATCH   = "Gölge-Nesne Eşleştirme";
    private static final String FIND_DIFFERENT = "Farklı Cisim Bulma";
    private static final String SELECT_CORRECT = "Doğru Nesneyi Seçme";

    private final ChildRepository childRepository;
    private final ActivityRepository activityRepository;
    private final ActivityResultRepository activityResultRepository;

    public VisualPerceptionReportService(
            ChildRepository childRepository,
            ActivityRepository activityRepository,
            ActivityResultRepository activityResultRepository) {
        this.childRepository = childRepository;
        this.activityRepository = activityRepository;
        this.activityResultRepository = activityResultRepository;
    }

    /**
     * Belirli bir çocuk için Görsel Algı kategorisinin tam raporunu üretir.
     *
     * @param childId Raporu istenen çocuğun ID'si
     * @return VisualPerceptionReportDto (3 oyunun tüm verileri)
     */
    @Transactional(readOnly = true)
    public VisualPerceptionReportDto generateReport(Long childId) {

        // 1. Çocuğu bul
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));

        // 2. Raporu oluştur
        VisualPerceptionReportDto report = new VisualPerceptionReportDto();
        report.setChildId(child.getId());
        report.setChildName(child.getName());

        // 3. Her oyun için raporu hesapla
        report.setShadowMatching(buildGameReport(child, SHADOW_MATCH));
        report.setFindDifferent(buildGameReport(child, FIND_DIFFERENT));
        report.setSelectCorrect(buildGameReport(child, SELECT_CORRECT));

        return report;
    }

    /**
     * Tek bir oyunun raporunu hesaplar.
     * Tüm oturumları alır, her birini GameSessionDto'ya çevirir,
     * ortalamaları hesaplar.
     *
     * @param child    Çocuk entity'si
     * @param gameName Oyun adı (ActivityService'deki isimle aynı olmalı)
     * @return GameReportDto
     */
    private GameReportDto buildGameReport(Child child, String gameName) {

        GameReportDto dto = new GameReportDto();
        dto.setGameName(gameName);

        // Aktiviteyi bul (currentLevel için)
        Optional<Activity> activityOpt = activityRepository.findByChildIdAndName(child.getId(), gameName);
        if (activityOpt.isPresent()) {
            Activity activity = activityOpt.get();
            dto.setActivityId(activity.getId());
            dto.setCurrentLevel(activity.getCurrentLevel());
        } else {
            // Oyun henüz bu çocuk için oluşturulmamış (nadir durum)
            dto.setCurrentLevel(0);
        }

        // Tüm oturumları tarih sırasıyla çek (eskiden yeniye)
        List<ActivityResult> results = activityResultRepository
                .findByChildAndGameNameOrderByPlayedAtAsc(child, gameName);

        if (results.isEmpty()) {
            // Hiç oynanmamış - boş rapor dön
            dto.setTotalSessionCount(0);
            dto.setAvgMistakes(0.0);
            dto.setAvgIndependenceScore(100.0);
            dto.setAvgDurationSeconds(0.0);
            dto.setSessions(new ArrayList<>());
            return dto;
        }

        // Her oturumu GameSessionDto'ya çevir
        List<GameSessionDto> sessions = new ArrayList<>();

        // Aynı level birden fazla oynandıysa retry sayısını takip et
        // level -> kaç kez oynandı
        java.util.Map<Integer, Integer> levelPlayCounts = new java.util.HashMap<>();

        int totalMistakes = 0;
        double totalIndependence = 0.0;
        int totalDuration = 0;

        for (int i = 0; i < results.size(); i++) {
            ActivityResult result = results.get(i);

            // levelPlayed artık ActivityResult'tan direkt geliyor
            int level = result.getLevelPlayed();

            // Retry sayısı: aynı level'da kaçıncı deneme
            levelPlayCounts.put(level, levelPlayCounts.getOrDefault(level, 0) + 1);
            int retryCount = levelPlayCounts.get(level) - 1; // ilk oynama = retry 0

            GameSessionDto session = GameSessionDto.from(
                    i + 1,                           // sessionNumber (1'den başlar)
                    level,                           // level
                    result.getMistakesMade(),        // mistakesMade → kırmızı bar
                    result.getParentHelpCount(),     // parentHelpCount
                    result.getTotalTargetCount(),    // totalTargetCount
                    result.getIndependenceScore(),   // independenceScore → mavi çizgi (%)
                    result.getDurationSeconds(),     // durationSeconds
                    result.getScore()                // score
            );
            session.setRetryCount(retryCount);
            sessions.add(session);

            // Ortalama hesabı için topla
            totalMistakes     += result.getMistakesMade();
            totalIndependence += result.getIndependenceScore();
            totalDuration     += result.getDurationSeconds();
        }

        // Ortalamaları hesapla
        int count = results.size();
        dto.setTotalSessionCount(count);
        dto.setAvgMistakes(roundToTwo((double) totalMistakes / count));
        dto.setAvgIndependenceScore(roundToTwo(totalIndependence / count));
        dto.setAvgDurationSeconds(roundToTwo((double) totalDuration / count));
        dto.setSessions(sessions);

        return dto;
    }

    /** İki ondalık basamağa yuvarla */
    private double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}