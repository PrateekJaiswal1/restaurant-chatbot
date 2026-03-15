package com.chatbot.controller;

import com.chatbot.dto.ChatRequest;
import com.chatbot.service.AnthropicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class ChatController {

    private final AnthropicService anthropicService;

    public ChatController(AnthropicService anthropicService) {
        this.anthropicService = anthropicService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest chatRequest) {
        log.info("Received chat request with {} messages", chatRequest.getMessages().size());

        try {
            String response = anthropicService.chat(chatRequest);

            Map<String, String> result = new HashMap<>();
            result.put("reply", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing chat request", e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("reply", "Sorry, something went wrong. Please try again later.");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Restaurant Chatbot API");
        return ResponseEntity.ok(response);
    }

}
