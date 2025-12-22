package com.otigo.auth_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender; // Gönderen

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver; // Alan

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // Mesaj içeriği

    @Column(nullable = false)
    private LocalDateTime sentAt; // Gönderilme zamanı

    private boolean isRead = false; // Okundu mu?

    // --- Constructor (Yapıcı Metot) ---
    public Message() {
    }

    // --- GETTER VE SETTER METOTLARI (Eksik olanlar bunlar) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) { // İşte aradığınız metot
        this.sender = sender;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) { // İşte aradığınız metot
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) { // İşte aradığınız metot
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) { // İşte aradığınız metot
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}