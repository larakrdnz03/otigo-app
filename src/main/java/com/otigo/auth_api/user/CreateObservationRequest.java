package com.otigo.auth_api.user; // ExpertService, User, Child ile aynı pakette

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

/**
 * Bu bir DTO (Data Transfer Object) sınıfıdır.
 * Uzman'ın mobil uygulamadan yeni bir gözlem eklerken
 * göndereceği veriyi taşımak için kullanılır.
 */
public class CreateObservationRequest {

    @NotEmpty(message = "Gözlem notları boş olamaz")
    private String notes; // Gözlem notları (örn: "Bugün 'elma' kelimesini kullandı.")
    
    private String activityName; // Aktivite adı (örn: "Serbest Oyun")

    // Gözlem tarihi. Eğer mobil uygulama göndermezse,
    // sunucuda (Service) kendimiz "LocalDateTime.now()" olarak atayabiliriz.
    private LocalDateTime observationDate;

    // --- Getter ve Setter'lar ---

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public LocalDateTime getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(LocalDateTime observationDate) {
        this.observationDate = observationDate;
    }
}