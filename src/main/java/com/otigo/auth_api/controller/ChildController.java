package com.otigo.auth_api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.entity.Child;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.service.ChildService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/children")
public class ChildController {

    private final ChildService childService;

    public ChildController(ChildService childService) {
        this.childService = childService;
    }

    /**
     * Veli kendi çocuğunu ekler.
     * POST /api/v1/children
     * Body: { "name": "Ayşe", "age": 6 } veya { "firstName": "Ayşe", "age": 6 }
     */
    @PostMapping
    public ResponseEntity<?> addChild(
            @Valid @RequestBody CreateChildRequest request,
            Authentication authentication) {
        try {
            UserEntity parent = (UserEntity) authentication.getPrincipal();
            // firstName veya name alanından birini kullan
            String childName = request.getFirstName() != null ? request.getFirstName() : request.getName();
            Child child = childService.addChild(parent, childName, request.getAge());
            return ResponseEntity.status(HttpStatus.CREATED).body(child);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Velinin kendi çocuklarını listeler.
     * GET /api/v1/children
     */
    @GetMapping
    public ResponseEntity<List<Child>> getMyChildren(Authentication authentication) {
        UserEntity parent = (UserEntity) authentication.getPrincipal();
        List<Child> children = childService.getChildrenForParent(parent);
        return ResponseEntity.ok(children);
    }

    public static class CreateChildRequest {
        private String name;
        private String firstName;
        private int age;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }
}