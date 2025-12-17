package com.otigo.auth_api.user; // Paketin neresiyse orayı yaz

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    // Belirli bir çocuğa ait oyunları getir
    List<Game> findByChildId(Long childId);

    // Belirli bir çocuğun, belirli bir oyununu bul (Örn: Çocuğun 'Puzzle' kaydı)
    Game findByChildIdAndGameName(Long childId, String gameName);
}