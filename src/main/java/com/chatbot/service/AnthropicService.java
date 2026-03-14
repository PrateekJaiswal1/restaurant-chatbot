package com.chatbot.service;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AnthropicService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url}")
    private String apiUrl;

    @Value("${anthropic.api.version}")
    private String apiVersion;

    @Value("${anthropic.model}")
    private String model;

    @Value("${anthropic.max-tokens}")
    private int maxTokens;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Restaurant system prompt - customize this for each client
    private static final String SYSTEM_PROMPT = """
You are a helpful AI assistant for Bella Roma, an Italian restaurant in Austin, Texas.

RESTAURANT INFO:
- Name: Bella Roma
- Address: 123 Congress Ave, Austin, TX 78701
- Phone: (512) 555-0198
- Hours: Mon-Thu 11am-10pm, Fri-Sat 11am-11pm, Sun 12pm-10pm
- Website: www.bellaroma.com

MENU HIGHLIGHTS:
- Appetizers: Bruschetta ($8), Calamari ($12), Garlic Bread ($6)
- Pasta: Fettuccine Alfredo ($14), Lasagna ($16), Ravioli ($15)
- Main Courses: Chicken Parmesan ($18), Eggplant Parmesan ($16), Seafood Risotto ($22)
- Desserts: Tiramisu ($7), Panna Cotta ($8)
- Wine Selection: Italian & Californian wines ($35-$80)

YOUR RESPONSIBILITIES:
1. Answer questions about menu, hours, location, reservations
2. Help customers make reservations (collect: name, date, time, party size, phone)
3. Answer dietary questions (vegetarian, vegan, gluten-free options available)
4. Handle general restaurant inquiries professionally
5. If you can't help, provide the restaurant phone number

TONE: Friendly, professional, Italian hospitality. Keep responses concise (2-3 sentences max).
""";

    public AnthropicService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public String chat(ChatRequest chatRequest) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                return "Error: ANTHROPIC_API_KEY environment variable not set. " +
                       "Please set it and restart the application.";
            }

            // Build request payload
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("system", SYSTEM_PROMPT);
            requestBody.put("messages", chatRequest.getMessages());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", apiVersion);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.debug("Calling Anthropic API with model: {}", model);

            // Call API
            ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                    apiUrl,
                    request,
                    ChatResponse.class
            );

            if (response.getBody() != null && response.getBody().getContent() != null
                    && !response.getBody().getContent().isEmpty()) {
                String responseText = response.getBody().getContent().get(0).getText();
                log.debug("Received response from Anthropic: {}", responseText);
                return responseText;
            } else {
                log.error("Empty response from Anthropic API");
                return "Sorry, I didn't understand that. Please try again.";
            }

        } catch (Exception e) {
            log.error("Error calling Anthropic API", e);
            return "Sorry, something went wrong. Please call us at (512) 555-0198";
        }
    }
}
