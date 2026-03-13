package com.otigo.auth_api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.response.ReportResponse;
import com.otigo.auth_api.dto.response.VisualPerceptionReportDto;
import com.otigo.auth_api.dto.response.MathSkillsReportDto;
import com.otigo.auth_api.service.ReportService;
import com.otigo.auth_api.service.VisualPerceptionReportService;
import com.otigo.auth_api.service.MathSkillsReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final VisualPerceptionReportService visualPerceptionReportService;
    private final MathSkillsReportService mathSkillsReportService;

    public ReportController(ReportService reportService,
                            VisualPerceptionReportService visualPerceptionReportService,
                            MathSkillsReportService mathSkillsReportService) {
        this.reportService = reportService;
        this.visualPerceptionReportService = visualPerceptionReportService;
        this.mathSkillsReportService = mathSkillsReportService;
    }

    @GetMapping("/{childId}")
    public ResponseEntity<ReportResponse> getReportData(
            @PathVariable Long childId,
            Authentication authentication) {
        ReportResponse response = reportService.generateReport(childId);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/{childId}/math-skills")
    public ResponseEntity<?> getMathSkillsReport(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            MathSkillsReportDto report =
                    mathSkillsReportService.generateReport(childId);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}