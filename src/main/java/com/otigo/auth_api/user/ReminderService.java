package com.otigo.auth_api.user; // Paketin neresiyse

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.otigo.auth_api.user.expert.ExpertRecommendation;
import com.otigo.auth_api.user.expert.ExpertRecommendationRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReminderService {

    private final ExpertRecommendationRepository recommendationRepository;
    private final ActivityRepository activityRepository; 
    private final JavaMailSender mailSender;

    public ReminderService(ExpertRecommendationRepository recommendationRepository,
                           ActivityRepository activityRepository,
                           JavaMailSender mailSender) {
        this.recommendationRepository = recommendationRepository;
        this.activityRepository = activityRepository;
        this.mailSender = mailSender;
    }

    /**
     * Her gün saat 12:00'de çalışır.
     * Cron formatı: saniye dakika saat gün ay haftanın_günü
     * "0 0 12 * * ?" -> Her gün 12:00:00
     */
    @Scheduled(cron = "0 0 12 * * ?") 
    @Transactional
    public void checkInactivityAndNotify() {
        System.out.println("⏰ Hatırlatıcı servisi çalıştı: " + LocalDateTime.now());

        // 1. Bitmemiş oyun görevlerini bul
        List<ExpertRecommendation> activeTasks = recommendationRepository.findByIsCompletedFalseAndActivityIsNotNull();

        for (ExpertRecommendation task : activeTasks) {
            
            // Görevdeki oyunu ve çocuğu bul
            Activity game = task.getActivity();
            
            // Eğer oyun hiç oynanmadıysa referans tarihi: Görevin verildiği tarih (createdAt)
            // Eğer oynandıysa referans tarihi: Son oynanma tarihi (lastPlayedAt)
            LocalDateTime lastActivityDate = game.getLastPlayedAt();
            
            if (lastActivityDate == null) {
                // Çocuk oyuna hiç başlamamış, görevin verildiği tarihe bak
                lastActivityDate = task.getCreatedAt();
            }

            // 2. Zaman farkını hesapla (Bugün - Son Aktivite)
            long daysBetween = ChronoUnit.DAYS.between(lastActivityDate, LocalDateTime.now());

            // 3. Eğer 4 gün veya daha fazla geçtiyse
            if (daysBetween >= 4) {
                // Veliye bildirim gönder
                sendReminderEmail(task, daysBetween);
            }
        }
    }

    private void sendReminderEmail(ExpertRecommendation task, long daysInactive) {
        try {
            // Çocuğun velisini bul (Child -> Parent ilişkisinden)
            // Not: Child entity'sinde parent'a erişimimiz olmalı.
            // Child.java içinde 'getParent()' olduğunu varsayıyorum. 
            // Eğer yoksa ChildRepository'den parent'ı bulman gerekebilir.
            UserEntity parent = task.getChild().getParent(); 
            
            if (parent == null || parent.getEmail() == null) return;

            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(parent.getEmail());
            email.setSubject("Hatırlatma: Tamamlanmamış Ödevler Var 📢");
            
            String message = String.format(
                "Sayın Veli %s,\n\n" +
                "%s isimli çocuğunuzun, Uzman tarafından atanan '%s' oyun görevinde son %d gündür ilerleme kaydedilmedi.\n\n" +
                "Mevcut Seviye: %d\n" +
                "Hedef Seviye: %d\n\n" +
                "Lütfen çocuğunuzu oyuna yönlendiriniz.\n" +
                "Otigo Ekibi",
                parent.getLastname(),
                task.getChild().getName(),
                task.getActivity().getName(),
                daysInactive,
                task.getActivity().getCurrentLevel(),
                task.getTargetLevel()
            );

            email.setText(message);
            mailSender.send(email);
            
            System.out.println("📧 Hatırlatma maili gönderildi: " + parent.getEmail());

        } catch (Exception e) {
            System.err.println("❌ Mail gönderilemedi: " + e.getMessage());
        }
    }
}