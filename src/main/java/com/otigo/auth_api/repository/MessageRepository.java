// MessageRepository.java
package com.otigo.auth_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.otigo.auth_api.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // İki kişi arasındaki konuşma geçmişini getir (Tarihe göre sıralı)
    // "Hem benim gönderdiklerim hem bana gelenler" mantığı
    // Bu sorgu biraz karmaşıktır, JPA ile şöyle yazabiliriz:
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) " +
           "OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) ORDER BY m.sentAt ASC")
    List<Message> findChatHistory(Long userId1, Long userId2);
    
    // Okunmamış mesajları getir (Bildirim için)
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
}