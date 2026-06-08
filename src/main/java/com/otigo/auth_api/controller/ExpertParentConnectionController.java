package com.otigo.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.response.ConnectionResponseDto;
import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.ExpertParentConnection;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.repository.UserRepository;
import com.otigo.auth_api.service.ExpertParentConnectionService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/connections")
public class ExpertParentConnectionController {

    private final ExpertParentConnectionService connectionService;
    private final UserRepository userRepository;

    public ExpertParentConnectionController(ExpertParentConnectionService connectionService,
                                            UserRepository userRepository) {
        this.connectionService = connectionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/request")
    public ResponseEntity<?> sendRequest(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            UserEntity parent = (UserEntity) authentication.getPrincipal();
            String expertEmail = body.get("expertEmail");
            ExpertParentConnection connection = connectionService.sendRequest(parent, expertEmail);
            return ResponseEntity.ok(ConnectionResponseDto.from(connection));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ConnectionResponseDto>> getPendingRequests(Authentication authentication) {
        UserEntity expert = (UserEntity) authentication.getPrincipal();
        List<ConnectionResponseDto> result = connectionService.getPendingRequests(expert)
                .stream().map(ConnectionResponseDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptRequest(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            UserEntity expert = (UserEntity) authentication.getPrincipal();
            ExpertParentConnection connection = connectionService.acceptRequest(expert, id);
            return ResponseEntity.ok(ConnectionResponseDto.from(connection));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            UserEntity expert = (UserEntity) authentication.getPrincipal();
            ExpertParentConnection connection = connectionService.rejectRequest(expert, id);
            return ResponseEntity.ok(ConnectionResponseDto.from(connection));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-experts")
    public ResponseEntity<List<ConnectionResponseDto>> getMyExperts(Authentication authentication) {
        UserEntity parent = (UserEntity) authentication.getPrincipal();
        List<ConnectionResponseDto> result = connectionService.getMyExperts(parent)
                .stream().map(ConnectionResponseDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-parents")
    public ResponseEntity<List<ConnectionResponseDto>> getMyParents(Authentication authentication) {
        UserEntity expert = (UserEntity) authentication.getPrincipal();
        List<ConnectionResponseDto> result = connectionService.getMyParents(expert)
                .stream().map(ConnectionResponseDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-children")
    public ResponseEntity<List<Child>> getMyChildren(Authentication authentication) {
        UserEntity expert = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(connectionService.getMyChildren(expert));
    }

    /**
     * Uzmanın detay bilgilerini getirir (veli panelinde uzmana tıklayınca).
     * GET /api/connections/expert/{expertId}
     */
    @GetMapping("/expert/{expertId}")
    public ResponseEntity<?> getExpertDetail(
            @PathVariable Long expertId,
            Authentication authentication) {
        try {
            UserEntity expert = userRepository.findById(expertId)
                    .orElseThrow(() -> new RuntimeException("Uzman bulunamadı."));

            return ResponseEntity.ok(Map.of(
                    "id", expert.getId(),
                    "firstname", expert.getFirstname() != null ? expert.getFirstname() : "",
                    "lastname", expert.getLastname() != null ? expert.getLastname() : "",
                    "email", expert.getEmail(),
                    "phoneNumber", expert.getPhoneNumber() != null ? expert.getPhoneNumber() : "",
                    "address", expert.getAddress() != null ? expert.getAddress() : ""
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Veli profil detayı (uzman için).
     * GET /api/connections/parent/{parentId}
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<?> getParentDetail(
            @PathVariable Long parentId,
            Authentication authentication) {
        try {
            UserEntity expert = (UserEntity) authentication.getPrincipal();
            UserEntity parent = userRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Veli bulunamadı."));

            boolean isConnected = connectionService.getMyParents(expert).stream()
                    .anyMatch(c -> c.getParent().getId().equals(parentId));

            if (!isConnected) {
                return ResponseEntity.status(403).body("Bu velinin profiline erişim yetkiniz yok.");
            }

            return ResponseEntity.ok(Map.of(
                    "id", parent.getId(),
                    "firstname", parent.getFirstname() != null ? parent.getFirstname() : "",
                    "lastname", parent.getLastname() != null ? parent.getLastname() : "",
                    "email", parent.getEmail(),
                    "phoneNumber", parent.getPhoneNumber() != null ? parent.getPhoneNumber() : ""
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}