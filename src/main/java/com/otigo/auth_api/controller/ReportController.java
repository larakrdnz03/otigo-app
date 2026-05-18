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
import com.otigo.auth_api.dto.response.MotorSkillsReportDto;
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
import com.otigo.auth_api.service.MotorSkillsReportService;
import com.otigo.auth_api.service.LanguageSkillsReportService;
import com.otigo.auth_api.service.MonthlyTrendService;
import com.otigo.auth_api.dto.response.LanguageSkillsReportDto;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final VisualPerceptionReportService visualPerceptionReportService;
    private final MathSkillsReportService mathSkillsReportService;
    private final MotorSkillsReportService motorSkillsReportService;
    private final LanguageSkillsReportService languageSkillsReportService;
    private final MonthlyTrendService monthlyTrendService;
    private final ChildRepository childRepository;
    private final ExpertParentConnectionRepository connectionRepository;

    public ReportController(ReportService reportService,
                            VisualPerceptionReportService visualPerceptionReportService,
                            MathSkillsReportService mathSkillsReportService,
                            MotorSkillsReportService motorSkillsReportService,
                            LanguageSkillsReportService languageSkillsReportService,
                            MonthlyTrendService monthlyTrendService,
                            ChildRepository childRepository,
                            ExpertParentConnectionRepository connectionRepository) {
        this.reportService = reportService;
        this.visualPerceptionReportService = visualPerceptionReportService;
        this.mathSkillsReportService = mathSkillsReportService;
        this.motorSkillsReportService = motorSkillsReportService;
        this.languageSkillsReportService = languageSkillsReportService;
        this.monthlyTrendService = monthlyTrendService;
        this.childRepository = childRepository;
        this.connectionRepository = connectionRepository;
    }

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
            return ResponseEntity.ok(reportService.generateReport(childId));
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
            return ResponseEntity.ok(visualPerceptionReportService.generateReport(childId));
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
            return ResponseEntity.ok(mathSkillsReportService.generateReport(childId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{childId}/motor-skills")
    public ResponseEntity<?> getMotorSkillsReport(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));
            if (!hasAccess(user, child)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu rapora erişim yetkiniz yok.");
            }
            return ResponseEntity.ok(motorSkillsReportService.generateReport(childId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{childId}/language-skills")
    public ResponseEntity<?> getLanguageSkillsReport(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));
            if (!hasAccess(user, child)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu rapora erişim yetkiniz yok.");
            }
            return ResponseEntity.ok(languageSkillsReportService.generateReport(childId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{childId}/monthly-trend")    public ResponseEntity<?> getMonthlyTrend(
            @PathVariable Long childId,
            Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Çocuk bulunamadı."));
            if (!hasAccess(user, child)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu rapora erişim yetkiniz yok.");
            }
            return ResponseEntity.ok(monthlyTrendService.generateTrend(childId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}