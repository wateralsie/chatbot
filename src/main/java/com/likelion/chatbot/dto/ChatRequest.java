package com.likelion.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequest {
    @NotBlank(message = "메시지를 입력해주세요.")
    private String message;
    @JsonProperty("conversation_id")
    private Long conversationId;
}
