package com.likelion.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {
    private String email;
    @JsonProperty("api_key")
    private String apiKey;
}
