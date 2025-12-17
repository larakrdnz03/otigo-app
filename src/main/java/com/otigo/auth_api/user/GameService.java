package com.otigo.auth_api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class GameService {

    @Autowired
    private  GameRepository gameRepository;


    // Sistemdeki Sabit Oyun İsimleri (Gereksinimlerden aldım)
    private final List<String> DEFAULT_GAMES = Arrays.asList(
        "Gölge-Nesne Eşleştirme", 
        "Labirent Takibi", 
        "Sayı-Nesne Eşleştirme", 
        "Puzzle", 
        "Renk Boyama"
    );

    /**
     * Yeni bir çocuk kaydolduğunda bu metodu çağıracağız.
     * Çocuğa tüm oyunları 1. seviyeden başlatarak oluşturur.
     */
    public void createInitialGamesForChild(Child child) {
        for (String gameName : DEFAULT_GAMES) {
            Game newGame = new Game(gameName, child);
            gameRepository.save(newGame);
        }
    }

    /**
     * Çocuğun oyun listesini getirir.
     */
    public List<Game> getGamesForChild(Long childId) {
        return gameRepository.findByChildId(childId);
    }

    // Çocuk oyunu bitirince bu metodu çağır
    public void updateLastPlayedTime(Long gameId) {
       Game game = gameRepository.findById(gameId).orElseThrow();
       game.setLastPlayedAt(LocalDateTime.now()); // <-- SAATİ GÜNCELLE
       gameRepository.save(game);
    }
}