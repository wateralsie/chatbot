package com.likelion.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatStreamingResponse {
    @JsonProperty("conversation_id")
    private Long conversationId;
    private String answer;
    private boolean done;
}
