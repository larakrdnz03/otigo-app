package com.otigo.auth_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.otigo.auth_api.entity.Activity;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.enums.ActivityType;
import com.otigo.auth_api.repository.ActivityRepository;

import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    private final List<String> GAME_NAMES = Arrays.asList(
        "Gölge-Nesne Eşleştirme",
        "Farklı Cisim Bulma",
        "Doğru Nesneyi Seçme",
        "Labirent Takibi",
        "Renk Boyama",
        "Sayı-Nesne Eşleştirme",
        "Yapboz",
        "Zıt Kavramlar"
    );

    private final List<String> EVENT_NAMES = Arrays.asList("Hikaye Dinleyip Soru Cevaplama");

    @Transactional
    public void createInitialActivitiesForChild(Child child) {
        for (String name : GAME_NAMES) {
            if (activityRepository.findByChildIdAndName(child.getId(), name).isEmpty()) {
                Activity game = new Activity(name, ActivityType.OYUN, child);
                activityRepository.save(game);
            }
        }

        for (String name : EVENT_NAMES) {
            if (activityRepository.findByChildIdAndName(child.getId(), name).isEmpty()) {
                Activity event = new Activity(name, ActivityType.ETKINLIK, child);
                activityRepository.save(event);
            }
        }
    }

    public List<Activity> getGamesForChild(Long childId) {
        return activityRepository.findByChildIdAndType(childId, ActivityType.OYUN);
    }

    public List<Activity> getEventsForChild(Long childId) {
        return activityRepository.findByChildIdAndType(childId, ActivityType.ETKINLIK);
    }

    public void updateLastPlayedTime(Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow();
        activity.setLastPlayedAt(java.time.LocalDateTime.now());
        activityRepository.save(activity);
    }
}