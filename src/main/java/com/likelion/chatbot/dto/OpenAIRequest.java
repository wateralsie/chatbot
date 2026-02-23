package com.likelion.chatbot.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenAIRequest {
    private String model;
    private List<OpenAIMessage> messages;
    private boolean stream;

    @Getter
    @Builder
    public static class OpenAIMessage {
        private String role;
        private String content;
    }
}
