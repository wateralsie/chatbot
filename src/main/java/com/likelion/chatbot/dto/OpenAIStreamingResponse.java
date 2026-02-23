package com.likelion.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OpenAIStreamingResponse {
    private List<Choice> choices;

    @Getter
    @NoArgsConstructor
    public static class Choice {
        private Delta delta;
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Getter
    @NoArgsConstructor
    public static class Delta {
        private String role;
        private String content;
    }
}
