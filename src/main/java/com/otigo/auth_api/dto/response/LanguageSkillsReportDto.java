package com.otigo.auth_api.dto.response;

/**
 * "Dil-İletişim Becerileri" kategorisinin tam raporunu taşır.
 *
 * Bu kategori 2 oyun içerir:
 * 1. Zıt Kavramlar - zıt kartları eşleştirememek hata sayılır
 * 2. Hikaye Dinleyip Soru Cevaplama - yanlış şık hata sayılır
 *
 * Endpoint: GET /api/reports/{childId}/language-skills
 */
public class LanguageSkillsReportDto {

    private Long childId;
    private String childName;
    private GameReportDto oppositeConcepts;
    private GameReportDto storyQA;

    public LanguageSkillsReportDto() {}

    public Long getChildId() { return childId; }
    public void setChildId(Long childId) { this.childId = childId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public GameReportDto getOppositeConcepts() { return oppositeConcepts; }
    public void setOppositeConcepts(GameReportDto oppositeConcepts) { this.oppositeConcepts = oppositeConcepts; }

    public GameReportDto getStoryQA() { return storyQA; }
    public void setStoryQA(GameReportDto storyQA) { this.storyQA = storyQA; }
}