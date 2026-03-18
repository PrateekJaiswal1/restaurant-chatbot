package com.chatbot.controller;

import com.chatbot.dto.ChatRequest;
import com.chatbot.service.AnthropicService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api")
public class ChatController {

    private final AnthropicService anthropicService;

    // Stores one bucket per IP address
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public ChatController(AnthropicService anthropicService) {
        this.anthropicService = anthropicService;
    }

    // Creates a rate limit bucket for an IP:
    // - Max 20 messages per hour
    // - Max 5 messages per minute (burst protection)
    private Bucket getBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(20, Refill.intervally(20, Duration.ofHours(1))))
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                .build());
    }

    // Gets the real IP even when behind Railway's proxy
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody ChatRequest chatRequest,
            HttpServletRequest request) {

        String clientIp = getClientIp(request);
        Bucket bucket = getBucket(clientIp);

        // Check rate limit — if exceeded return 429
        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            Map<String, String> rateLimitResponse = new HashMap<>();
            rateLimitResponse.put("reply", "You've sent too many messages. Please wait a minute and try again.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(rateLimitResponse);
        }

        log.info("Received chat request from {} with {} messages", clientIp, chatRequest.getMessages().size());

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