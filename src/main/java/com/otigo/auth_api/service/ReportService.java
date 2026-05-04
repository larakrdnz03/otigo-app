package com.otigo.auth_api.service;

import com.lowagie.text.*; 
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.otigo.auth_api.dto.response.ReportResponse;
import com.otigo.auth_api.entity.ActivityResult;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.ExpertRecommendation;
import com.otigo.auth_api.entity.SymptomSurvey;
import com.otigo.auth_api.entity.enums.ActivityType;
import com.otigo.auth_api.repository.ActivityResultRepository;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.ExpertRecommendationRepository;
import com.otigo.auth_api.repository.SymptomSurveyRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ChildRepository childRepository;
    private final ActivityResultRepository activityResultRepository;
    private final SymptomSurveyRepository symptomSurveyRepository;
    private final ExpertRecommendationRepository recommendationRepository;

    public ReportService(ChildRepository childRepository,
                         ActivityResultRepository activityResultRepository,
                         SymptomSurveyRepository symptomSurveyRepository,
                         ExpertRecommendationRepository recommendationRepository) {
        this.childRepository = childRepository;
        this.activityResultRepository = activityResultRepository;
        this.symptomSurveyRepository = symptomSurveyRepository;
        this.recommendationRepository = recommendationRepository;
    }

    @Transactional(readOnly = true)
    public ReportResponse generateReport(Long childId) {

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı"));

        List<ActivityResult> allResults = activityResultRepository.findByChildOrderByPlayedAtDesc(child);

        List<ActivityResult> gameResults = allResults.stream()
                .filter(r -> r.getActivity().getType() == ActivityType.OYUN)
                .collect(Collectors.toList());

        List<ActivityResult> eventResults = allResults.stream()
                .filter(r -> r.getActivity().getType() == ActivityType.ETKINLIK)
                .collect(Collectors.toList());

        List<SymptomSurvey> surveys = symptomSurveyRepository.findByChildOrderBySurveyDateDesc(child);
        List<ExpertRecommendation> recommendations = recommendationRepository.findByChildOrderByCreatedAtDesc(child);

        Map<String, Double> avgMistakes = new java.util.HashMap<>();
        Map<String, Integer> totalMistakes = new java.util.HashMap<>();
        Map<String, Integer> gameCounts = new java.util.HashMap<>();

        for (ActivityResult result : gameResults) {
            String gameName = result.getActivity().getName();
            int mistakes = result.getMistakesMade();
            totalMistakes.put(gameName, totalMistakes.getOrDefault(gameName, 0) + mistakes);
            gameCounts.put(gameName, gameCounts.getOrDefault(gameName, 0) + 1);
        }

        for (String name : totalMistakes.keySet()) {
            double average = (double) totalMistakes.get(name) / gameCounts.get(name);
            avgMistakes.put(name, average);
        }

        ReportResponse report = new ReportResponse();
        report.setGameResultsHistory(gameResults);
        report.setEventResultsHistory(eventResults);
        report.setSurveyHistory(surveys);
        report.setRecommendationHistory(recommendations);
        report.setAverageMistakesByGame(avgMistakes);

        return report;
    }

    public byte[] exportReportToPdf(Long childId) {
        ReportResponse data = generateReport(childId);
        Child child = childRepository.findById(childId).orElseThrow();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Gelisim Raporu - " + child.getName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("1. Oyun Performansi (Ortalama Hata)", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            document.add(new Paragraph("\n"));

            if (data.getAverageMistakesByGame().isEmpty()) {
                document.add(new Paragraph("Henüz oyun verisi yok."));
            } else {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                addTableHeader(table, "Oyun Adi");
                addTableHeader(table, "Ortalama Hata");

                for (Map.Entry<String, Double> entry : data.getAverageMistakesByGame().entrySet()) {
                    table.addCell(entry.getKey());
                    table.addCell(String.format("%.2f", entry.getValue()));
                }
                document.add(table);
            }
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("2. Tamamlanan Etkinlikler", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            if (data.getEventResultsHistory().isEmpty()) {
                document.add(new Paragraph("Henüz tamamlanan etkinlik yok."));
            } else {
                com.lowagie.text.List list = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
                for (ActivityResult event : data.getEventResultsHistory()) {
                    String itemText = event.getActivity().getName() +
                                      " - " +
                                      event.getPlayedAt().toLocalDate();
                    list.add(new ListItem(itemText));
                }
                document.add(list);
            }
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("3. Uzman Tavsiyeleri", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            if (data.getRecommendationHistory().isEmpty()) {
                document.add(new Paragraph("Henüz uzman yorumu yok."));
            } else {
                for (ExpertRecommendation rec : data.getRecommendationHistory()) {
                    document.add(new Paragraph("- " + rec.getRecommendationText()));
                    document.add(new Paragraph("  (Tarih: " + rec.getCreatedAt().toLocalDate() + ")", FontFactory.getFont(FontFactory.HELVETICA, 10)));
                }
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF hatasi: " + e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setPhrase(new Phrase(headerTitle));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}