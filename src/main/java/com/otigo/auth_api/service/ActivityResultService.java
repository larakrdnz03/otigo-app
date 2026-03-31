package com.otigo.auth_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.dto.request.CreateActivityResultRequest;
import com.otigo.auth_api.entity.Activity;
import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.repository.ActivityRepository;
import com.otigo.auth_api.repository.ActivityResultRepository;
import com.otigo.auth_api.repository.ChildRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityResultService {

    private final ActivityResultRepository activityResultRepository;
    private final ChildRepository childRepository;
    private final ActivityRepository activityRepository;

    public ActivityResultService(ActivityResultRepository activityResultRepository,
                                  ChildRepository childRepository,
                                  ActivityRepository activityRepository) {
        this.activityResultRepository = activityResultRepository;
        this.childRepository = childRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public ActivityResult saveActivityResult(Long childId, CreateActivityResultRequest request) {

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));

        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new RuntimeException("Aktivite bulunamadı. ID: " + request.getActivityId()));

        ActivityResult newResult = new ActivityResult();
        newResult.setChild(child);
        newResult.setActivity(activity);
        newResult.setScore(request.getScore());
        newResult.setDurationSeconds(request.getDurationSeconds());
        newResult.setMistakesMade(request.getMistakesMade());
        newResult.setParentHelped(request.isParentHelped());
        newResult.setParentHelpCount(request.getParentHelpCount());
        newResult.setTotalTargetCount(request.getTotalTargetCount());
        newResult.setLevelPlayed(request.getLevelPlayed());
        newResult.setPlayedAt(
            request.getPlayedAt() != null ? request.getPlayedAt() : LocalDateTime.now()
        );

        if (request.getLevelResults() != null && !request.getLevelResults().isEmpty()) {
            newResult.setLevelResults(request.getLevelResults());
        }

        double independenceScore = calculateIndependenceScore(
            request.getParentHelpCount(),
            request.getTotalTargetCount()
        );
        newResult.setIndependenceScore(independenceScore);

        if (request.getParentHelpCount() > 0) {
            newResult.setParentHelped(true);
        }

        return activityResultRepository.save(newResult);
    }

    public double calculateIndependenceScore(int helpCount, int totalTargets) {
        if (totalTargets <= 0) return 100.0;
        if (helpCount <= 0) return 100.0;
        if (helpCount >= totalTargets) return 0.0;

        double score = (1.0 - (double) helpCount / totalTargets) * 100.0;
        return Math.max(0.0, Math.min(100.0, score));
    }

    @Transactional(readOnly = true)
    public List<ActivityResult> getActivityResultsForChild(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı. ID: " + childId));
        return activityResultRepository.findByChildOrderByPlayedAtDesc(child);
    }
}