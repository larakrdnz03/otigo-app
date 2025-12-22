// MessageController.java
package com.otigo.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.otigo.auth_api.dto.request.CreateMessageRequest;
import com.otigo.auth_api.entity.Message;
import com.otigo.auth_api.entity.UserEntity;
import com.otigo.auth_api.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Mesaj Gönder
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(Authentication authentication, 
                                         @RequestBody CreateMessageRequest request) { // DTO oluşturmanız gerekecek
        UserEntity sender = (UserEntity) authentication.getPrincipal();
        try {
            Message msg = messageService.sendMessage(sender, request.getReceiverId(), request.getContent());
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Geçmişi Getir
    @GetMapping("/history/{otherUserId}")
    public ResponseEntity<List<Message>> getHistory(Authentication authentication, 
                                                    @PathVariable Long otherUserId) {
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();
        List<Message> history = messageService.getChatHistory(currentUser.getId(), otherUserId);
        return ResponseEntity.ok(history);
    }
}