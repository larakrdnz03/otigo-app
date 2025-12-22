package com.otigo.auth_api.entity; // Child ve User ile aynı pakette

// User ve Child sınıflarını import etmemize GEREK YOK (çünkü aynı paketteler)
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "observations")
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime observationDate; // Gözlemin yapıldığı tarih ve saat

    @Lob // Uzun metinler için (Large Object)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String notes; // Gözlem notları
    
    //private String activityName; // Gözlem sırasındaki aktivite (örn: "Oyun", "Konuşma")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = true) 
    private Activity activity;
    /**
     * Bu gözlem HANGİ ÇOCUK için yapıldı?
     * Bir çocuğun birden fazla gözlemi olabilir (One-to-Many).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child; // 'Child' aynı pakette olduğu için import gerekmez

    /**
     * Bu gözlemi HANGİ UZMAN yaptı?
     * Bir uzmanın birden fazla gözlemi olabilir (One-to-Many).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_user_id", nullable = false)
    private UserEntity expert; // 'User' aynı pakette olduğu için import gerekmez
    
    // --- Constructor'lar ---
    
    public Observation() {
        // JPA için boş constructor
    }

    // --- Getter ve Setter'lar ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(LocalDateTime observationDate) {
        this.observationDate = observationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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
}