package com.likelion.chatbot.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OpenAIResponse {
    private List<Choice> choices;

    @Getter
    @NoArgsConstructor
    public static class Choice {
        private Message message;
    }

    @Getter
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
