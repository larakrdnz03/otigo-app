package com.otigo.auth_api.dto.response;

/**
 * "Görsel Algı ve Dikkat Becerileri" kategorisinin tam raporunu taşır.
 *
 * Bu kategori 3 oyun içerir:
 * 1. Gölge-Nesne Eşleştirme
 * 2. Farklı Cisim Bulma
 * 3. Doğru Nesneyi Seçme
 *
 * Frontend bu DTO'yu alarak her oyun için ayrı grafik çizer.
 * (Görseldeki "Zorluk (Parça Sayısı) ve Hata Analizi" grafiği gibi)
 *
 * Endpoint: GET /api/reports/{childId}/visual-perception
 */
public class VisualPerceptionReportDto {

    /** Çocuğun ID'si */
    private Long childId;

    /** Çocuğun adı */
    private String childName;

    /**
     * Oyun 1: Gölge-Nesne Eşleştirme
     * 8 level, her level'da farklı nesne sayısı
     * Hata: yanlış eşleştirme sayısı
     */
    private GameReportDto shadowMatching;

    /**
     * Oyun 2: Farklı Cisim Bulma
     * 6 level
     * Hata: yanlış görsel tıklama
     */
    private GameReportDto findDifferent;

    /**
     * Oyun 3: Doğru Nesneyi Seçme
     * 6 kategori x 6 level
     * Hata: yanlış görsel seçimi
     * NOT: Bu oyunun kategorileri ayrı ayrı raporlanabilir.
     * Şimdilik tüm kategorilerin toplamı olarak döner.
     */
    private GameReportDto selectCorrect;

    // --- Constructor ---
    public VisualPerceptionReportDto() {}

    // --- Getter ve Setter'lar ---
    public Long getChildId() { return childId; }
    public void setChildId(Long childId) { this.childId = childId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public GameReportDto getShadowMatching() { return shadowMatching; }
    public void setShadowMatching(GameReportDto shadowMatching) { this.shadowMatching = shadowMatching; }

    public GameReportDto getFindDifferent() { return findDifferent; }
    public void setFindDifferent(GameReportDto findDifferent) { this.findDifferent = findDifferent; }

    public GameReportDto getSelectCorrect() { return selectCorrect; }
    public void setSelectCorrect(GameReportDto selectCorrect) { this.selectCorrect = selectCorrect; }
}