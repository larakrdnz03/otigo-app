package com.otigo.auth_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.dto.response.MonthlyTrendDto;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.repository.ActivityResultRepository;
import com.otigo.auth_api.repository.ChildRepository;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonthlyTrendService {

    // Görsel Algı oyunları
    private static final List<String> VISUAL_GAMES = List.of(
            "Gölge-Nesne Eşleştirme", "Farklı Cisim Bulma", "Doğru Nesneyi Seçme");

    // Motor Becerileri oyunları
    private static final List<String> MOTOR_GAMES = List.of(
            "Labirent Takibi", "Renk Boyama");

    // Matematik oyunları
    private static final List<String> MATH_GAMES = List.of(
            "Sayı-Nesne Eşleştirme", "Yapboz");

    // Dil-İletişim oyunları
    private static final List<String> LANGUAGE_GAMES = List.of(
            "Kelime Düzeltme", "Zıt Kavramlar", "Hikaye Dinleyip Soru Cevaplama");

    private final ChildRepository childRepository;
    private final ActivityResultRepository activityResultRepository;

    public MonthlyTrendService(ChildRepository childRepository,
                               ActivityResultRepository activityResultRepository) {
        this.childRepository = childRepository;
        this.activityResultRepository = activityResultRepository;
    }

    @Transactional(readOnly = true)
    public MonthlyTrendDto generateTrend(Long childId) {

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));

        // Son 6 ayı hesapla
        LocalDateTime now = LocalDateTime.now();
        List<String> months = new ArrayList<>();
        List<LocalDateTime> monthStarts = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            monthStarts.add(monthStart);
            months.add(monthStart.getMonth().getDisplayName(TextStyle.FULL, new Locale("tr")));
        }

        // Tüm activity_results çek
        List<ActivityResult> allResults = activityResultRepository.findByChildOrderByPlayedAtDesc(child);

        MonthlyTrendDto dto = new MonthlyTrendDto();
        dto.setChildId(child.getId());
        dto.setChildName(child.getName());
        dto.setMonths(months);
        dto.setVisualPerception(calculateMonthlyAvg(allResults, VISUAL_GAMES, monthStarts, now));
        dto.setMotorSkills(calculateMonthlyAvg(allResults, MOTOR_GAMES, monthStarts, now));
        dto.setMathSkills(calculateMonthlyAvg(allResults, MATH_GAMES, monthStarts, now));
        dto.setLanguageSkills(calculateMonthlyAvg(allResults, LANGUAGE_GAMES, monthStarts, now));

        return dto;
    }

    private List<Double> calculateMonthlyAvg(
            List<ActivityResult> allResults,
            List<String> gameNames,
            List<LocalDateTime> monthStarts,
            LocalDateTime now) {

        List<Double> monthlyAvgs = new ArrayList<>();

        for (int i = 0; i < monthStarts.size(); i++) {
            LocalDateTime start = monthStarts.get(i);
            LocalDateTime end = (i < monthStarts.size() - 1) ? monthStarts.get(i + 1) : now;

            List<ActivityResult> monthResults = allResults.stream()
                    .filter(r -> r.getPlayedAt() != null
                            && !r.getPlayedAt().isBefore(start)
                            && r.getPlayedAt().isBefore(end)
                            && gameNames.contains(r.getActivity().getName()))
                    .collect(Collectors.toList());

            if (monthResults.isEmpty()) {
                monthlyAvgs.add(null); // Veri yok
            } else {
                double avg = monthResults.stream()
                        .mapToDouble(ActivityResult::getIndependenceScore)
                        .average()
                        .orElse(0.0);
                monthlyAvgs.add(Math.round(avg * 100.0) / 100.0);
            }
        }

        return monthlyAvgs;
    }
}