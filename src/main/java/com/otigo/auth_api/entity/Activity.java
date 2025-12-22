package com.otigo.auth_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.otigo.auth_api.entity.enums.ActivityType;

@Entity
@Table(name = "activites")
public class   Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) // Veritabanına yazı olarak (GAME/EVENT) kaydeder
    @Column(nullable = false)
    private ActivityType type;

    @Column(nullable = false)
    private int currentLevel = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @OneToMany(mappedBy="activity", fetch = FetchType.LAZY)
    private List<ExpertRecommendation> recommendations ; 

    private LocalDateTime lastPlayedAt;

    // --- 1. BOŞ CONSTRUCTOR (JPA İÇİN ŞART) ---
    public Activity() {
    }

    // --- 2. PARAMETRELİ CONSTRUCTOR (SENİN HATANIN ÇÖZÜMÜ BURASI) ---
    public Activity(String name, ActivityType type,Child child) {
        this.name = name;
        this.type = type;
        this.child = child;
        this.currentLevel = 1;
    }

    // --- Getter ve Setter'lar ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }

    public LocalDateTime getLastPlayedAt() { return lastPlayedAt; }
    public void setLastPlayedAt(LocalDateTime lastPlayedAt) { this.lastPlayedAt = lastPlayedAt; }
}