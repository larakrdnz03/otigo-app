package com.otigo.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.ExpertParentConnection;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.service.ExpertParentConnectionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/connections")
public class ExpertParentConnectionController {

    private final ExpertParentConnectionService connectionService;

    public ExpertParentConnectionController(ExpertParentConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Veli uzman emailini girerek bağlantı isteği gönderir.
     * POST /api/connections/request
     * Body: { "expertEmail": "uzman@mail.com" }
     */
    @PostMapping("/request")
    public ResponseEntity<?> sendRequest(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            UserEntity parent = (UserEntity) authentication.getPrincipal();
            String expertEmail = body.get("expertEmail");
            ExpertParentConnection connection = connectionService.sendRequest(parent, expertEmail);
            return ResponseEntity.ok(connection);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Uzman bekleyen istekleri görür.
     * GET /api/connections/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ExpertParentConnection>> getPendingRequests(Authentication authentication) {
        UserEntity expert = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(connectionService.getPendingRequests(expert));
    }

    /**
     * Uzman isteği kabul eder.
     * PUT /api/connections/{id}/accept
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptRequest(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            UserEntity expert = (UserEntity) authentication.getPrincipal();
            ExpertParentConnection connection = connectionService.acceptRequest(expert, id);
            return ResponseEntity.ok(connection);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Uzman isteği reddeder.
     * PUT /api/connections/{id}/reject
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            UserEntity expert = (UserEntity) authentication.getPrincipal();
            ExpertParentConnection connection = connectionService.rejectRequest(expert, id);
            return ResponseEntity.ok(connection);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Velinin bağlı uzmanlarını getirir.
     * GET /api/connections/my-experts
     */
    @GetMapping("/my-experts")
    public ResponseEntity<List<ExpertParentConnection>> getMyExperts(Authentication authentication) {
        UserEntity parent = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(connectionService.getMyExperts(parent));
    }

    /**
     * Uzmanın bağlı velilerini getirir.
     * GET /api/connections/my-parents
     */
    @GetMapping("/my-parents")
    public ResponseEntity<List<ExpertParentConnection>> getMyParents(Authentication authentication) {
        UserEntity expert = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(connectionService.getMyParents(expert));
    }

    /**
     * Uzmanın takip ettiği çocukları getirir.
     * GET /api/connections/my-children
     */
    @GetMapping("/my-children")
    public ResponseEntity<List<Child>> getMyChildren(Authentication authentication) {
        UserEntity expert = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(connectionService.getMyChildren(expert));
    }
}