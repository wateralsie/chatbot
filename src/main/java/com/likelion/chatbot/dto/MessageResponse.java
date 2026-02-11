package com.likelion.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.chatbot.entity.MessageEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResponse {
    private Long id;
    private String role;
    private String content;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static MessageResponse from(MessageEntity entity) {
        return MessageResponse.builder()
                .id(entity.getId())
                .role(entity.getRole())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
