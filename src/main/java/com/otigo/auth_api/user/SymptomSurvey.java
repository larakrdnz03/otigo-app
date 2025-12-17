package com.otigo.auth_api.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "symptom_surveys")
public class SymptomSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Bu anketin hangi çocuğa ait olduğunu belirtir.
     * Bir çocuğun birden fazla anketi olabilir (One-to-Many).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    /**
     * Bu anketi hangi velinin (veya uzmanın?) doldurduğunu belirtir.
     * Bir kullanıcının birden fazla anketi olabilir (One-to-Many).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filled_by_user_id", nullable = false)
    private UserEntity filledBy; // Anketi dolduran kullanıcı (Veli)

    /**
     * Anketin doldurulduğu tarih.
     */
    @Column(nullable = false)
    private LocalDateTime surveyDate;

    /**
     * Anketin cevaplarını esnek bir formatta (JSON) saklar.
     * Mobil uygulama bize örn: "{ \"soru_1\": 5, \"soru_2\": 3, \"notlar\": \"...\" }"
     * şeklinde bir metin gönderir, biz de bunu kaydederiz.
     */
    @Lob // Uzun metin alanı (Large Object)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String surveyResultsJson;

    // --- Constructor'lar ---

    public SymptomSurvey() {
        // JPA için boş constructor
    }

    // --- Getter ve Setter'lar ---
    // (Tüm alanlar için getter ve setter'ları ekleyin)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public UserEntity getFilledBy() {
        return filledBy;
    }

    public void setFilledBy(UserEntity filledBy) {
        this.filledBy = filledBy;
    }

    public LocalDateTime getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(LocalDateTime surveyDate) {
        this.surveyDate = surveyDate;
    }

    public String getSurveyResultsJson() {
        return surveyResultsJson;
    }

    public void setSurveyResultsJson(String surveyResultsJson) {
        this.surveyResultsJson = surveyResultsJson;
    }
}