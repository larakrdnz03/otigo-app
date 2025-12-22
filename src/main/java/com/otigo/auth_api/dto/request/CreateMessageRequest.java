package com.otigo.auth_api.dto.request;

// Bu sınıf, mobil uygulamadan (Frontend) gelen JSON verisini karşılar.
// { "receiverId": 5, "content": "Merhaba" } gibi bir veri bekliyoruz.
public class CreateMessageRequest {

    private Long receiverId; // Mesajı kime atıyoruz? (ID'si)
    private String content;  // Mesajın içeriği ne?

    // --- Getter ve Setter Metotları ---

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}