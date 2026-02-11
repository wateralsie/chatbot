package com.likelion.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.chatbot.entity.ConversationEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversationResponse {
    private Long id;
    private String title;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static ConversationResponse from(ConversationEntity entity) {
        return ConversationResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
