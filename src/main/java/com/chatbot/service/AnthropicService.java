package com.chatbot.service;

import com.chatbot.dto.ChatRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

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

    @Value("${restaurant.name:Tony's Pizzeria}")
    private String restaurantName;

    @Value("${restaurant.phone:(512) 555-0198}")
    private String restaurantPhone;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private EventOfferService eventOfferService;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AnthropicService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public String chat(ChatRequest chatRequest) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                return "Error: ANTHROPIC_API_KEY environment variable not set.";
            }

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("system", buildSystemPrompt());
            requestBody.set("messages", objectMapper.valueToTree(chatRequest.getMessages()));
            requestBody.set("tools", buildToolsDefinition());

            String reply = callClaude(requestBody);
            JsonNode responseJson = objectMapper.readTree(reply);
            String stopReason = responseJson.get("stop_reason").asText();

            if ("tool_use".equals(stopReason)) {
                return handleToolUse(chatRequest.getMessages(), responseJson);
            }

            return responseJson.get("content").get(0).get("text").asText();

        } catch (Exception e) {
            log.error("Error calling Anthropic API", e);
            return "Sorry, something went wrong. Please call us at " + restaurantPhone;
        }
    }

    private String handleToolUse(List<ChatRequest.Message> originalMessages,
                                 JsonNode assistantResponse) throws Exception {
        for (JsonNode block : assistantResponse.get("content")) {
            if ("tool_use".equals(block.get("type").asText())) {
                String toolName  = block.get("name").asText();
                String toolUseId = block.get("id").asText();
                JsonNode input   = block.get("input");
                log.debug("Claude calling tool: {}", toolName);
                String toolResult = executeTool(toolName, input);
                return sendToolResult(originalMessages, assistantResponse, toolUseId, toolResult);
            }
        }
        return "I couldn't process that. Please try again.";
    }

    private String executeTool(String toolName, JsonNode input) {
        return switch (toolName) {
            case "check_availability" -> reservationService.checkAvailability(
                    input.get("date").asText(),
                    input.get("time").asText(),
                    input.get("party_size").asInt()
            );
            case "make_reservation" -> reservationService.makeReservation(
                    input.get("customer_name").asText(),
                    input.get("customer_phone").asText(),
                    input.get("date").asText(),
                    input.get("time").asText(),
                    input.get("party_size").asInt()
            );
            case "cancel_reservation" -> reservationService.cancelReservation(
                    input.get("reservation_id").asLong()
            );
            case "get_todays_specials" -> reservationService.getTodaysSpecials();
            case "get_todays_offers"   -> eventOfferService.getTodaysOffers();
            case "get_upcoming_events" -> eventOfferService.getUpcomingEvents();
            default -> "Unknown tool: " + toolName;
        };
    }

    private String sendToolResult(List<ChatRequest.Message> originalMessages,
                                  JsonNode assistantResponse,
                                  String toolUseId,
                                  String toolResult) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("system", buildSystemPrompt());
        requestBody.set("tools", buildToolsDefinition());

        ArrayNode messages = objectMapper.createArrayNode();

        for (ChatRequest.Message msg : originalMessages) {
            ObjectNode m = objectMapper.createObjectNode();
            m.put("role", msg.getRole());
            m.put("content", msg.getContent());
            messages.add(m);
        }

        ObjectNode assistantMsg = objectMapper.createObjectNode();
        assistantMsg.put("role", "assistant");
        assistantMsg.set("content", assistantResponse.get("content"));
        messages.add(assistantMsg);

        ObjectNode toolResultMsg = objectMapper.createObjectNode();
        toolResultMsg.put("role", "user");
        ArrayNode toolResultContent = objectMapper.createArrayNode();
        ObjectNode toolResultBlock = objectMapper.createObjectNode();
        toolResultBlock.put("type", "tool_result");
        toolResultBlock.put("tool_use_id", toolUseId);
        toolResultBlock.put("content", toolResult);
        toolResultContent.add(toolResultBlock);
        toolResultMsg.set("content", toolResultContent);
        messages.add(toolResultMsg);

        requestBody.set("messages", messages);

        String response = callClaude(requestBody);
        JsonNode responseJson = objectMapper.readTree(response);
        return responseJson.get("content").get(0).get("text").asText();
    }

    private String callClaude(ObjectNode requestBody) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", apiVersion);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(requestBody), headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
        return response.getBody();
    }

    private String buildSystemPrompt() {
        return String.format("""
        You are a warm and friendly assistant for %s, an authentic Italian pizzeria in Austin, Texas.
        
        PERSONALITY:
        - Speak like a welcoming Italian host — warm, enthusiastic, and helpful
        - Use phrases like "Benvenuto!", "Perfetto!", "Excellent choice!" occasionally
        - Keep responses short and conversational — max 3-4 sentences
        - Always end with a question to keep the conversation going
        - Never say "I cannot help" — always offer an alternative
        
        RESTAURANT INFO:
        - Phone: %s
        - Hours: Mon-Thu 11am-10pm, Fri-Sat 11am-11pm, Sun 12pm-10pm
        - Address: 123 Congress Ave, Austin, TX 78701
        - Parking: Free street parking available outside
        
        MENU HIGHLIGHTS:
        - Pizzas: Margherita ($14), Pepperoni ($16), BBQ Chicken ($17), Veggie Supreme ($15)
        - Pasta: Fettuccine Alfredo ($14), Lasagna ($16), Ravioli ($15)
        - Appetizers: Bruschetta ($8), Calamari ($12), Garlic Bread ($6)
        - Desserts: Tiramisu ($7), Panna Cotta ($8)
        - Dietary: Vegetarian, vegan, gluten-free options available on request
        
        UPSELLING (do this naturally, not pushy):
        - When someone books for dinner → mention today's specials
        - When someone asks about pizza → suggest adding Garlic Bread ($6)
        - When someone asks about desserts → recommend Tiramisu — our most popular!
        - When someone books for 2 on weekend → mention our couples wine pairing ($25)
        
        HANDLING COMPLAINTS:
        - If someone complains about anything → apologize sincerely first
        - Offer discount code SORRY10 for 10%% off their next visit
        - Never argue — always stay positive and helpful
        
        RESERVATION RULES:
        - Always confirm: name, phone, date, time, party size before booking
        - For groups of 8 or more → ask them to call us directly at %s
        - Last reservation is 30 minutes before closing time
        - If a slot is full → immediately suggest 2 alternative times
        
        You have real-time tools to:
        1. Check table availability
        2. Make reservations (saves to database instantly)
        3. Cancel existing reservations
        4. Get today's specials from the kitchen
        5. Get today's special offers and discounts
        6. Get upcoming events and live music schedule
        
        Today's date is: %s
        """,
                restaurantName, restaurantPhone, restaurantPhone, LocalDate.now()
        );
    }

    private JsonNode buildToolsDefinition() throws Exception {
        String toolsJson = """
            [
              {
                "name": "check_availability",
                "description": "Check if tables are available for a given date, time, and party size",
                "input_schema": {
                  "type": "object",
                  "properties": {
                    "date":       { "type": "string",  "description": "Date in YYYY-MM-DD format" },
                    "time":       { "type": "string",  "description": "Time in HH:MM 24hr format" },
                    "party_size": { "type": "integer", "description": "Number of guests" }
                  },
                  "required": ["date", "time", "party_size"]
                }
              },
              {
                "name": "make_reservation",
                "description": "Create a new table reservation for a customer",
                "input_schema": {
                  "type": "object",
                  "properties": {
                    "customer_name":  { "type": "string",  "description": "Full name" },
                    "customer_phone": { "type": "string",  "description": "Phone number" },
                    "date":           { "type": "string",  "description": "Date in YYYY-MM-DD format" },
                    "time":           { "type": "string",  "description": "Time in HH:MM 24hr format" },
                    "party_size":     { "type": "integer", "description": "Number of guests" }
                  },
                  "required": ["customer_name", "customer_phone", "date", "time", "party_size"]
                }
              },
              {
                "name": "cancel_reservation",
                "description": "Cancel an existing reservation by booking ID",
                "input_schema": {
                  "type": "object",
                  "properties": {
                    "reservation_id": { "type": "integer", "description": "Booking ID number" }
                  },
                  "required": ["reservation_id"]
                }
              },
              {
                "name": "get_todays_specials",
                "description": "Get today's special dishes and prices",
                "input_schema": { "type": "object", "properties": {}, "required": [] }
              },
              {
                "name": "get_todays_offers",
                "description": "Get today's special offers and discounts",
                "input_schema": { "type": "object", "properties": {}, "required": [] }
              },
              {
                "name": "get_upcoming_events",
                "description": "Get upcoming events, live music, and entertainment schedule",
                "input_schema": { "type": "object", "properties": {}, "required": [] }
              }
            ]
            """;
        return objectMapper.readTree(toolsJson);
    }
}
