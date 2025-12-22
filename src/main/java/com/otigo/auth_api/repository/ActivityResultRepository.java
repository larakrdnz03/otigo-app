package com.otigo.auth_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.enums.ActivityType;

import java.util.List;

@Repository
public interface ActivityResultRepository extends JpaRepository<ActivityResult, Long> {

    /**
     * Bir çocuğa ait tüm oyun sonuçlarını, oynanma tarihine göre
     * en yeniden eskiye doğru (Desc) sıralı olarak getirir.
     * * * Mobil uygulamanın "Gelişim Raporu" grafiğini çizmek için
     * * ihtiyaç duyacağı veri sorgusu budur.
     */
    List<ActivityResult> findByChildOrderByPlayedAtDesc(Child child);

    /**
     * EKSTRA SÜPER METOT: 🚀
     * Eğer ilerde "Bana çocuğun sadece OYUN sonuçlarını ver" veya 
     * "Sadece ETKİNLİK sonuçlarını ver" demek istersen bunu kullanabilirsin.
     * * Spring Data JPA, 'Activity_Type' yazınca otomatik olarak 
     * Result -> Activity tablosuna gidip oradaki Type'a bakar.
     */
    List<ActivityResult> findByChildAndActivity_TypeOrderByPlayedAtDesc(Child child, ActivityType type);
}