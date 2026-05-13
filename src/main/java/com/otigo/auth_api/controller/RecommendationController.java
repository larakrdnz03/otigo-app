package com.otigo.auth_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.entity.Activity;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.ExpertParentConnection;
import com.otigo.auth_api.entity.ExpertParentConnection.ConnectionStatus;
import com.otigo.auth_api.entity.ExpertRecommendation;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.ActivityRepository;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.ExpertParentConnectionRepository;
import com.otigo.auth_api.repository.ExpertRecommendationRepository;
import com.otigo.auth_api.service.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final ExpertRecommendationRepository recommendationRepository;
    private final ChildRepository childRepository;
    private final ActivityRepository activityRepository;
    private final ExpertParentConnectionRepository connectionRepository;
    private final NotificationService notificationService;

    public RecommendationController(
            ExpertRecommendationRepository recommendationRepository,
            ChildRepository childRepository,
            ActivityRepository activityRepository,
            ExpertParentConnectionRepository connectionRepository,
            NotificationService notificationService) {
        this.recommendationRepository = recommendationRepository;
        this.childRepository = childRepository;
        this.activityRepository = activityRepository;
        this.connectionRepository = connectionRepository;
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<?> createRecommendation(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        try {
            UserEntity expert = (UserEntity) authentication.getPrincipal();

            Long childId = Long.valueOf(body.get("childId").toString());
            Long activityId = Long.valueOf(body.get("activityId").toString());
            Integer targetLevel = Integer.valueOf(body.get("targetLevel").toString());

            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));

            UserEntity parent = child.getParent();
            boolean isConnected = connectionRepository
                    .findByExpertAndParent(expert, parent)
                    .map(c -> c.getStatus() == ConnectionStatus.ACCEPTED)
                    .orElse(false);

            if (!isConnected) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Bu çocuğa ödev verme yetkiniz yok.");
            }

            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new RuntimeException("Aktivite bulunamadı."));

            ExpertRecommendation recommendation = new ExpertRecommendation();
            recommendation.setChild(child);
            recommendation.setExpert(expert);
            recommendation.setActivity(activity);
            recommendation.setTargetLevel(targetLevel);
            recommendation.setCompleted(false);

            ExpertRecommendation saved = recommendationRepository.save(recommendation);

            // Veliye push notification gönder
            String expertName = expert.getFirstname() != null ? expert.getFirstname() : "Uzman";
            notificationService.sendNotification(
                    parent,
                    "Yeni Ödev",
                    expertName + " " + child.getName() + " için yeni bir ödev atadı: " + activity.getName()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", saved.getId(),
                    "childId", child.getId(),
                    "childName", child.getName(),
                    "activityId", activity.getId(),
                    "activityName", activity.getName(),
                    "targetLevel", saved.getTargetLevel(),
                    "isCompleted", saved.isCompleted(),
                    "createdAt", saved.getCreatedAt().toString()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<?> getChildRecommendations(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));

            List<ExpertRecommendation> recommendations =
                    recommendationRepository.findByChildOrderByCreatedAtDesc(child);

            List<Map<String, Object>> result = recommendations.stream().map(r -> Map.<String, Object>of(
                    "id", r.getId(),
                    "activityId", r.getActivity().getId(),
                    "activityName", r.getActivity().getName(),
                    "targetLevel", r.getTargetLevel(),
                    "isCompleted", r.isCompleted(),
                    "createdAt", r.getCreatedAt().toString()
            )).toList();

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-assignments")
    public ResponseEntity<?> getMyAssignments(Authentication authentication) {
        UserEntity expert = (UserEntity) authentication.getPrincipal();

        List<ExpertRecommendation> recommendations =
                recommendationRepository.findByExpertOrderByCreatedAtDesc(expert);

        List<Map<String, Object>> result = recommendations.stream().map(r -> Map.<String, Object>of(
                "id", r.getId(),
                "childId", r.getChild().getId(),
                "childName", r.getChild().getName(),
                "activityId", r.getActivity().getId(),
                "activityName", r.getActivity().getName(),
                "targetLevel", r.getTargetLevel(),
                "isCompleted", r.isCompleted(),
                "createdAt", r.getCreatedAt().toString()
        )).toList();

        return ResponseEntity.ok(result);
    }
}