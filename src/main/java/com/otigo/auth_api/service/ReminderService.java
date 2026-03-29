package com.otigo.auth_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.entity.Activity;
import com.otigo.auth_api.entity.ExpertRecommendation;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.ActivityRepository;
import com.otigo.auth_api.repository.ExpertRecommendationRepository;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReminderService {

    private final ExpertRecommendationRepository recommendationRepository;
    private final ActivityRepository activityRepository;
    private final Resend resend;

    public ReminderService(ExpertRecommendationRepository recommendationRepository,
                           ActivityRepository activityRepository,
                           @Value("${resend.api.key}") String resendApiKey) {
        this.recommendationRepository = recommendationRepository;
        this.activityRepository = activityRepository;
        this.resend = new Resend(resendApiKey);
    }

    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional
    public void checkInactivityAndNotify() {
        System.out.println("⏰ Hatırlatıcı servisi çalıştı: " + LocalDateTime.now());

        List<ExpertRecommendation> activeTasks = recommendationRepository.findByIsCompletedFalseAndActivityIsNotNull();

        for (ExpertRecommendation task : activeTasks) {
            Activity game = task.getActivity();
            LocalDateTime lastActivityDate = game.getLastPlayedAt();

            if (lastActivityDate == null) {
                lastActivityDate = task.getCreatedAt();
            }

            long daysBetween = ChronoUnit.DAYS.between(lastActivityDate, LocalDateTime.now());

            if (daysBetween >= 4) {
                sendReminderEmail(task, daysBetween);
            }
        }
    }

    private void sendReminderEmail(ExpertRecommendation task, long daysInactive) {
        try {
            UserEntity parent = task.getChild().getParent();

            if (parent == null || parent.getEmail() == null) return;

            String message = String.format(
                "Sayın Veli %s,\n\n" +
                "%s isimli çocuğunuzun, Uzman tarafından atanan '%s' oyun görevinde son %d gündür ilerleme kaydedilmedi.\n\n" +
                "Mevcut Seviye: %d\n" +
                "Hedef Seviye: %d\n\n" +
                "Lütfen çocuğunuzu oyuna yönlendiriniz.\n\n" +
                "Sevgiler,\nOtigo Ekibi",
                parent.getLastname(),
                task.getChild().getName(),
                task.getActivity().getName(),
                daysInactive,
                task.getActivity().getCurrentLevel(),
                task.getTargetLevel()
            );

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("OTIGO Destek <destek@otigo.info>")
                    .to(parent.getEmail())
                    .subject("Hatırlatma: Tamamlanmamış Ödevler Var 📢")
                    .text(message)
                    .build();

            resend.emails().send(params);
            System.out.println("📧 Hatırlatma maili gönderildi: " + parent.getEmail());

        } catch (Exception e) {
            System.err.println("❌ Mail gönderilemedi: " + e.getMessage());
        }
    }
}