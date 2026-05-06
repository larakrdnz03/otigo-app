package com.otigo.auth_api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.response.ReportResponse;
import com.otigo.auth_api.dto.response.VisualPerceptionReportDto;
import com.otigo.auth_api.dto.response.MathSkillsReportDto;
import com.otigo.auth_api.dto.response.MonthlyTrendDto;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.ExpertParentConnection.ConnectionStatus;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.UserRole;
import com.otigo.auth_api.repository.ChildRepository;
import com.otigo.auth_api.repository.ExpertParentConnectionRepository;
import com.otigo.auth_api.service.ReportService;
import com.otigo.auth_api.service.VisualPerceptionReportService;
import com.otigo.auth_api.service.MathSkillsReportService;
import com.otigo.auth_api.service.MonthlyTrendService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final VisualPerceptionReportService visualPerceptionReportService;
    private final MathSkillsReportService mathSkillsReportService;
    private final MonthlyTrendService monthlyTrendService;
    private final ChildRepository childRepository;
    private final ExpertParentConnectionRepository connectionRepository;

    public ReportController(ReportService reportService,
                            VisualPerceptionReportService visualPerceptionReportService,
                            MathSkillsReportService mathSkillsReportService,
                            MonthlyTrendService monthlyTrendService,
                            ChildRepository childRepository,
                            ExpertParentConnectionRepository connectionRepository) {
        this.reportService = reportService;
        this.visualPerceptionReportService = visualPerceptionReportService;
        this.mathSkillsReportService = mathSkillsReportService;
        this.monthlyTrendService = monthlyTrendService;
        this.childRepository = childRepository;
        this.connectionRepository = connectionRepository;
    }

    /**
     * Kullanıcının bu çocuğa erişim yetkisi var mı kontrol eder.
     * Veli: kendi çocuğu olmalı
     * Uzman: bağlı olduğu velinin çocuğu olmalı
     */
    private boolean hasAccess(UserEntity user, Child child) {
        if (user.getRole() == UserRole.VELI) {
            return child.getParent().getId().equals(user.getId());
        }
        if (user.getRole() == UserRole.UZMAN) {
            return connectionRepository
                    .findByExpertAndParent(user, child.getParent())
                    .map(c -> c.getStatus() == ConnectionStatus.ACCEPTED)
                    .orElse(false);
        }
        return false;
    }

    @GetMapping("/{childId}")
    public ResponseEntity<?> getReportData(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));
            if (!hasAccess(user, child)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu rapora erişim yetkiniz yok.");
            }
            ReportResponse response = reportService.generateReport(childId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download/{childId}")
    public ResponseEntity<?> downloadPdfReport(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));
            if (!hasAccess(user, child)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu rapora erişim yetkiniz yok.");
            }
            byte[] pdfData = reportService.exportReportToPdf(childId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "gelisim_raporu_" + childId + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdfData);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{childId}/visual-perception")
    public ResponseEntity<?> getVisualPerceptionReport(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));
            if (!hasAccess(user, child)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu rapora erişim yetkiniz yok.");
            }
            VisualPerceptionReportDto report = visualPerceptionReportService.generateReport(childId);
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
            UserEntity user = (UserEntity) authentication.getPrincipal();
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));
            if (!hasAccess(user, child)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu rapora erişim yetkiniz yok.");
            }
            MathSkillsReportDto report = mathSkillsReportService.generateReport(childId);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{childId}/monthly-trend")
    public ResponseEntity<?> getMonthlyTrend(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));
            if (!hasAccess(user, child)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu rapora erişim yetkiniz yok.");
            }
            MonthlyTrendDto trend = monthlyTrendService.generateTrend(childId);
            return ResponseEntity.ok(trend);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}