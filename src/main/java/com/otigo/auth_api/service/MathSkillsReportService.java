package com.otigo.auth_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.dto.response.GameReportDto;
import com.otigo.auth_api.dto.response.GameSessionDto;
import com.otigo.auth_api.dto.response.MathSkillsReportDto;
import com.otigo.auth_api.entity.Activity;
import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.repository.ActivityRepository;
import com.otigo.auth_api.repository.ActivityResultRepository;
import com.otigo.auth_api.repository.ChildRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MathSkillsReportService {

    private static final String NUMBER_MATCHING = "Sayı-Nesne Eşleştirme";
    private static final String PUZZLE          = "Yapboz";

    private final ChildRepository childRepository;
    private final ActivityRepository activityRepository;
    private final ActivityResultRepository activityResultRepository;

    public MathSkillsReportService(
            ChildRepository childRepository,
            ActivityRepository activityRepository,
            ActivityResultRepository activityResultRepository) {
        this.childRepository = childRepository;
        this.activityRepository = activityRepository;
        this.activityResultRepository = activityResultRepository;
    }

    @Transactional(readOnly = true)
    public MathSkillsReportDto generateReport(Long childId) {

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));

        MathSkillsReportDto report = new MathSkillsReportDto();
        report.setChildId(child.getId());
        report.setChildName(child.getName());
        report.setNumberObjectMatching(buildGameReport(child, NUMBER_MATCHING));
        report.setPuzzle(buildGameReport(child, PUZZLE));

        return report;
    }

    private GameReportDto buildGameReport(Child child, String gameName) {

        GameReportDto dto = new GameReportDto();
        dto.setGameName(gameName);

        Optional<Activity> activityOpt = activityRepository.findByChildIdAndName(child.getId(), gameName);
        if (activityOpt.isPresent()) {
            Activity activity = activityOpt.get();
            dto.setActivityId(activity.getId());
            dto.setCurrentLevel(activity.getCurrentLevel());
        } else {
            dto.setCurrentLevel(0);
        }

        List<ActivityResult> results = activityResultRepository
                .findByChildAndGameNameOrderByPlayedAtAsc(child, gameName);

        if (results.isEmpty()) {
            dto.setTotalSessionCount(0);
            dto.setAvgMistakes(0.0);
            dto.setAvgIndependenceScore(100.0);
            dto.setAvgDurationSeconds(0.0);
            dto.setSessions(new ArrayList<>());
            return dto;
        }

        List<GameSessionDto> sessions = new ArrayList<>();
        java.util.Map<Integer, Integer> levelPlayCounts = new java.util.HashMap<>();

        int totalMistakes = 0;
        double totalIndependence = 0.0;
        int totalDuration = 0;

        for (int i = 0; i < results.size(); i++) {
            ActivityResult result = results.get(i);
            int level = result.getLevelPlayed();

            levelPlayCounts.put(level, levelPlayCounts.getOrDefault(level, 0) + 1);
            int retryCount = levelPlayCounts.get(level) - 1;

            GameSessionDto session = GameSessionDto.from(
                    i + 1,
                    level,
                    result.getMistakesMade(),
                    result.getParentHelpCount(),
                    result.getTotalTargetCount(),
                    result.getIndependenceScore(),
                    result.getDurationSeconds()
            );
            session.setRetryCount(retryCount);
            sessions.add(session);

            totalMistakes     += result.getMistakesMade();
            totalIndependence += result.getIndependenceScore();
            totalDuration     += result.getDurationSeconds();
        }

        int count = results.size();
        dto.setTotalSessionCount(count);
        dto.setAvgMistakes(roundToTwo((double) totalMistakes / count));
        dto.setAvgIndependenceScore(roundToTwo(totalIndependence / count));
        dto.setAvgDurationSeconds(roundToTwo((double) totalDuration / count));
        dto.setSessions(sessions);

        return dto;
    }

    private double roundToTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}