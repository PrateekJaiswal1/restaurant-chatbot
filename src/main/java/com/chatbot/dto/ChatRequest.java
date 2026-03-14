package com.chatbot.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private List<Message> messages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;  // "user" or "assistant"
        private String content;
    }
}
