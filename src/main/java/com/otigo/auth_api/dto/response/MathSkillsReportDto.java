package com.otigo.auth_api.dto.response;

public class MathSkillsReportDto {

    private Long childId;
    private String childName;
    private GameReportDto puzzle;
    private GameReportDto numberObjectMatching;

    public MathSkillsReportDto() {}

    public MathSkillsReportDto(Long childId, String childName,
                                GameReportDto puzzle,
                                GameReportDto numberObjectMatching) {
        this.childId = childId;
        this.childName = childName;
        this.puzzle = puzzle;
        this.numberObjectMatching = numberObjectMatching;
    }

    public Long getChildId() { return childId; }
    public void setChildId(Long childId) { this.childId = childId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public GameReportDto getPuzzle() { return puzzle; }
    public void setPuzzle(GameReportDto puzzle) { this.puzzle = puzzle; }

    public GameReportDto getNumberObjectMatching() { return numberObjectMatching; }
    public void setNumberObjectMatching(GameReportDto numberObjectMatching) { this.numberObjectMatching = numberObjectMatching; }
}