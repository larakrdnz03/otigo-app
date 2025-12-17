package com.otigo.auth_api.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.otigo.auth_api.user.expert.ExpertRecommendation;

@Entity
@Table(name = "games")
public class   Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String gameName;

    @Column(nullable = false)
    private int currentLevel = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @OneToMany(mappedBy="game", fetch = FetchType.LAZY)
    private List<ExpertRecommendation> recommendations ; 

    private LocalDateTime lastPlayedAt;

    // --- 1. BOŞ CONSTRUCTOR (JPA İÇİN ŞART) ---
    public Game() {
    }

    // --- 2. PARAMETRELİ CONSTRUCTOR (SENİN HATANIN ÇÖZÜMÜ BURASI) ---
    public Game(String gameName, Child child) {
        this.gameName = gameName;
        this.child = child;
        this.currentLevel = 1;
    }

    // --- Getter ve Setter'lar ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }

    public LocalDateTime getLastPlayedAt() { return lastPlayedAt; }
    public void setLastPlayedAt(LocalDateTime lastPlayedAt) { this.lastPlayedAt = lastPlayedAt; }
}