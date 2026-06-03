package com.otigo.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.entity.enums.UserRole;
import com.otigo.auth_api.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/experts")
public class ExpertController {

    private final UserRepository userRepository;

    public ExpertController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Uzman ara (isim veya email ile).
     * GET /api/v1/experts/search?q=...
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchExperts(@RequestParam String q) {
        List<UserEntity> experts = userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.UZMAN)
                .filter(u -> {
                    String query = q.toLowerCase();
                    String fullName = ((u.getFirstname() != null ? u.getFirstname() : "") + " " +
                                      (u.getLastname() != null ? u.getLastname() : "")).toLowerCase();
                    String email = u.getEmail().toLowerCase();
                    return fullName.contains(query) || email.contains(query);
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> result = experts.stream().map(e -> Map.<String, Object>of(
                "id", e.getId(),
                "firstname", e.getFirstname() != null ? e.getFirstname() : "",
                "lastname", e.getLastname() != null ? e.getLastname() : "",
                "email", e.getEmail(),
                "phoneNumber", e.getPhoneNumber() != null ? e.getPhoneNumber() : "",
                "address", e.getAddress() != null ? e.getAddress() : ""
        )).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Uzman profil detayı.
     * GET /api/v1/experts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getExpertById(@PathVariable Long id) {
        try {
            UserEntity expert = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Uzman bulunamadı."));

            if (expert.getRole() != UserRole.UZMAN) {
                return ResponseEntity.badRequest().body("Bu kullanıcı bir uzman değil.");
            }

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
     * Email ile uzman bul.
     * GET /api/v1/experts/by-email?email=...
     */
    @GetMapping("/by-email")
    public ResponseEntity<?> getExpertByEmail(@RequestParam String email) {
        try {
            UserEntity expert = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Uzman bulunamadı."));

            if (expert.getRole() != UserRole.UZMAN) {
                return ResponseEntity.badRequest().body("Bu kullanıcı bir uzman değil.");
            }

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
}