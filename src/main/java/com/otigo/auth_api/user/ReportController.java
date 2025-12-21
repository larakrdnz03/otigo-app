package com.otigo.auth_api.user;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // ... (Diğer endpointler) ...

    /**
     * 1. RAPOR VERİSİNİ GETİR (JSON)
     * Mobil uygulama "Gelişim Raporu" sayfasına ilk girdiğinde buraya istek atar.
     * Dönen veri içinde:
     * - gameResultsHistory (Grafik için)
     * - eventResultsHistory (Liste için)
     * - expertRecommendations (Yorumlar)
     * bulunur.
     */
    @GetMapping("/{childId}")
    public ResponseEntity<ReportResponse> getReportData(@PathVariable Long childId) {
        // ReportService içindeki ayrıştırma mantığı burada çalışır
        ReportResponse response = reportService.generateReport(childId);
        return ResponseEntity.ok(response);
    }

    // PDF İNDİRME ENDPOINT'İ
    @GetMapping("/download/{childId}")
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable Long childId) {
        // 1. PDF verisini servisten al
        byte[] pdfData = reportService.exportReportToPdf(childId);

        // 2. Dosya indirme başlıklarını ayarla
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Dosya adı: rapor_cocukID.pdf
        headers.setContentDispositionFormData("attachment", "gelisim_raporu_" + childId + ".pdf");

        // 3. Dosyayı gönder
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }
}