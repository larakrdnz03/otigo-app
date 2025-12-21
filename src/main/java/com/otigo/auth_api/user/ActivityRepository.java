package com.otigo.auth_api.user; // Paketin neresiyse orayı yaz

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    // Belirli bir çocuğa ait aktivitileri getir
    List<Activity> findByChildId(Long childId);

    // Belirli bir çocuğun, belirli bir oyununu bul (Örn: Çocuğun 'Puzzle' kaydı)
    List<Activity> findByChildIdAndType(Long childId, ActivityType type);

    Optional<Activity> findByChildIdAndName(Long childId, String name);
}