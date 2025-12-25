package com.otigo.auth_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "expert_recommendations")
public class ExpertRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi Çocuk?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    // Hangi Uzman?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_user_id", nullable = false)
    private UserEntity expert;

    // Tavsiye Metni
    @Column(columnDefinition = "TEXT")
    private String recommendationText;

    // --- GÖREV ALANLARI ---

    // Hangi Oyun Önerildi? (Nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = true) 
    private Activity activity;

    // Hedef Seviye Kaç? (Nullable)
    private Integer targetLevel; 

    // Görev Tamamlandı mı?
    private boolean isCompleted = false;

    // Oluşturulma Tarihi (Tek ve geçerli tarih alanı)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Boş Constructor (JPA için şart)
    public ExpertRecommendation() {
    }

    // --- Getter ve Setter'lar (Manuel) ---

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

    public UserEntity getExpert() { 
        return expert; 
    }
    public void setExpert(UserEntity expert) { 
        this.expert = expert; 
    }

    public String getRecommendationText() { 
        return recommendationText; 
    }
    public void setRecommendationText(String recommendationText) { 
        this.recommendationText = recommendationText; 
    }

    public Activity getActivity() { 
        return activity; 
    }
    public void setActivity(Activity activity) { 
        this.activity = activity; 
    }

    public Integer getTargetLevel() { 
        return targetLevel; 
    }
    public void setTargetLevel(Integer targetLevel) { 
        this.targetLevel = targetLevel; 
    }

    // boolean için getter genelde "is" ile başlar
    public boolean isCompleted() { 
        return isCompleted; 
    }
    public void setCompleted(boolean completed) { 
        isCompleted = completed; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    // Veritabanına kaydedilmeden hemen önce tarihi otomatik atar
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}