package com.otigo.auth_api.repository; // veya com.otigo.auth_api.observation

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.Observation;
import com.otigo.auth_api.entity.UserEntity;

import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {
    // Bir çocuğa ait tüm gözlemleri tarihe göre sıralı bul
    List<Observation> findByChildOrderByObservationDateDesc(Child child);

    // Bir uzmanın girdiği tüm gözlemleri bul
    List<Observation> findByExpert(UserEntity expert);
}