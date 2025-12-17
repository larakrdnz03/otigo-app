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