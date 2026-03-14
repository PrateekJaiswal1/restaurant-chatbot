package com.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String id;
    private String type;
    private String role;
    private List<ContentBlock> content;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentBlock {
        private String type;
        private String text;
    }
}
