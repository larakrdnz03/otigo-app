package com.otigo.auth_api.user;

import com.lowagie.text.*; // PDF kütüphanesi
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    // ... (Eski Repository tanımları ve Constructor AYNEN KALACAK) ...
    private final ChildRepository childRepository;
    private final GameResultRepository gameResultRepository;
    private final SymptomSurveyRepository symptomSurveyRepository;
    private final ExpertRecommendationRepository recommendationRepository;

    public ReportService(ChildRepository childRepository, GameResultRepository gameResultRepository, SymptomSurveyRepository symptomSurveyRepository, ExpertRecommendationRepository recommendationRepository) {
        this.childRepository = childRepository;
        this.gameResultRepository = gameResultRepository;
        this.symptomSurveyRepository = symptomSurveyRepository;
        this.recommendationRepository = recommendationRepository;
    }

    // ... (Eski generateReport metodu AYNEN KALACAK) ...
    @Transactional(readOnly = true)
    public ReportResponse generateReport(Long childId) {
        // ... (Sizin yazdığınız kodların aynısı burada duracak) ...
        Child child = childRepository.findById(childId).orElseThrow();
        List<GameResult> gameResults = gameResultRepository.findByChildOrderByPlayedAtDesc(child);
        List<SymptomSurvey> surveys = symptomSurveyRepository.findByChildOrderBySurveyDateDesc(child);
        //List<ExpertRecommendation> recommendations = recommendationRepository.findByChildOrderByRecommendationDateDesc(child);
        List<ExpertRecommendation> recommendations = recommendationRepository.findByChildOrderByCreatedAtDesc(child);



        Map<String, Double> avgMistakes = gameResults.stream()
                .collect(Collectors.groupingBy(GameResult::getGameName, Collectors.averagingInt(GameResult::getMistakesMade)));

        ReportResponse report = new ReportResponse();
        report.setGameResultsHistory(gameResults);
        report.setSurveyHistory(surveys);
        report.setRecommendationHistory(recommendations);
        report.setAverageMistakesByGame(avgMistakes);
        return report;
    }

    // --- YENİ EKLENEN PDF METODU ---

    /**
     * Verileri alır ve bir PDF dosyası (byte dizisi) olarak geri döndürür.
     */
    public byte[] exportReportToPdf(Long childId) {
        // 1. Önce veriyi hazırla (Mevcut metodunuzu çağırıyoruz)
        ReportResponse data = generateReport(childId);
        Child child = childRepository.findById(childId).orElseThrow();

        // 2. PDF Dokümanını oluştur
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // --- Başlık ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Gelisim Raporu - " + child.getName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n")); // Boşluk

            // --- Bölüm 1: Oyun Analizi (Tablo) ---
            document.add(new Paragraph("1. Oyun Performans Analizi (Ortalama Hata)", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(2); // 2 Sütunlu tablo
            table.setWidthPercentage(100);
            
            // Tablo Başlıkları
            addTableHeader(table, "Oyun Adi");
            addTableHeader(table, "Ortalama Hata Sayisi");

            // Tablo Verileri
            for (Map.Entry<String, Double> entry : data.getAverageMistakesByGame().entrySet()) {
                table.addCell(entry.getKey());
                table.addCell(String.format("%.2f", entry.getValue()));
            }
            document.add(table);
            document.add(new Paragraph("\n"));

            // --- Bölüm 2: Uzman Yorumları ---
            document.add(new Paragraph("2. Uzman Tavsiyeleri ve Yorumlar", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            for (ExpertRecommendation rec : data.getRecommendationHistory()) {
                document.add(new Paragraph("- " + rec.getRecommendationText()));
                document.add(new Paragraph("  (Tarih: " + rec.getCreatedAt().toLocalDate() + ")", FontFactory.getFont(FontFactory.HELVETICA, 10)));
            }

            document.close();
            return out.toByteArray(); // PDF dosyasını byte olarak döndür

        } catch (Exception e) {
            throw new RuntimeException("PDF oluşturulurken hata çıktı: " + e.getMessage());
        }
    }

    // Tablo başlığı eklemek için yardımcı metot
    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setPhrase(new Phrase(headerTitle));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}