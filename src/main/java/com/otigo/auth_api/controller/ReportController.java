package com.otigo.auth_api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.response.ReportResponse;
import com.otigo.auth_api.dto.response.VisualPerceptionReportDto;
import com.otigo.auth_api.service.ReportService;
import com.otigo.auth_api.service.VisualPerceptionReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final VisualPerceptionReportService visualPerceptionReportService;

    public ReportController(ReportService reportService,
                            VisualPerceptionReportService visualPerceptionReportService) {
        this.reportService = reportService;
        this.visualPerceptionReportService = visualPerceptionReportService;
    }

    /**
     * Genel gelişim raporu (mevcut, değişmedi)
     * GET /api/reports/{childId}
     */
    @GetMapping("/{childId}")
    public ResponseEntity<ReportResponse> getReportData(
            @PathVariable Long childId,
            Authentication authentication) {
        ReportResponse response = reportService.generateReport(childId);
        return ResponseEntity.ok(response);
    }

    /**
     * PDF indirme (mevcut, değişmedi)
     * GET /api/reports/download/{childId}
     */
    @GetMapping("/download/{childId}")
    public ResponseEntity<byte[]> downloadPdfReport(
            @PathVariable Long childId,
            Authentication authentication) {
        byte[] pdfData = reportService.exportReportToPdf(childId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "gelisim_raporu_" + childId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfData);
    }

    /**
     * YENİ: Görsel Algı ve Dikkat Becerileri kategorisi raporu
     *
     * Dönen JSON örneği:
     * {
     *   "childId": 1,
     *   "childName": "Ayşe",
     *   "shadowMatching": {
     *     "gameName": "Gölge-Nesne Eşleştirme",
     *     "currentLevel": 4,
     *     "totalSessionCount": 6,
     *     "avgMistakes": 2.17,
     *     "avgIndependenceScore": 78.5,
     *     "sessions": [
     *       { "sessionNumber": 1, "level": 1, "mistakesMade": 2,
     *         "independenceScore": 50.0, "parentHelpCount": 1,
     *         "totalTargetCount": 2, "durationSeconds": 45 },
     *       ...
     *     ]
     *   },
     *   "findDifferent": { ... },
     *   "selectCorrect": { ... }
     * }
     *
     * GET /api/reports/{childId}/visual-perception
     */
    @GetMapping("/{childId}/visual-perception")
    public ResponseEntity<?> getVisualPerceptionReport(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            VisualPerceptionReportDto report =
                    visualPerceptionReportService.generateReport(childId);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}