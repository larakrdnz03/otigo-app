package com.otigo.auth_api.user.expert;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.otigo.auth_api.user.Child;
import com.otigo.auth_api.user.Activity;
import com.otigo.auth_api.user.UserEntity;

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

    // Tavsiye Metni (Örn: "Dikkatini toplaması için bu oyunu verdim.")
    @Column(columnDefinition = "TEXT")
    private String recommendationText;

    // --- YENİ EKLENEN GÖREV ALANLARI ---

    // Hangi Oyun Önerildi? (Nullable: Her tavsiye oyun olmak zorunda değil)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = true) 
    private Activity activity;

    // Hedef Seviye Kaç? (Nullable)
    // Uzman: "Level 5'e gelene kadar oynasın" diyebilir.
    private Integer targetLevel; 

    // Görev Tamamlandı mı?
    // False: Çocuk hala bu görevi yapıyor (Kilitler aktif).
    // True: Çocuk hedef levele ulaştı veya sadece sözel bir tavsiyeydi.
    private boolean isCompleted = false;

    private LocalDateTime createdAt;

     // ✅ EKLEDİĞİMİZ ALAN
    /*@Column(nullable = false)
    private LocalDateTime recommendationDate;
   */

    public ExpertRecommendation() {}

    // --- Getter ve Setter'lar ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }

    public UserEntity getExpert() { return expert; }
    public void setExpert(UserEntity expert) { this.expert = expert; }

    public String getRecommendationText() { return recommendationText; }
    public void setRecommendationText(String recommendationText) { this.recommendationText = recommendationText; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    public Integer getTargetLevel() { return targetLevel; }
    public void setTargetLevel(Integer targetLevel) { this.targetLevel = targetLevel; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /*public LocalDateTime getRecommendationDate() {
       return recommendationDate;
    }*/

    /*public void setRecommendationDate(LocalDateTime recommendationDate) {
       this.recommendationDate = recommendationDate;
    }*/
   @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}