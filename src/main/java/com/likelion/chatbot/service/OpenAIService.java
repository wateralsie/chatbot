package com.likelion.chatbot.service;

import com.likelion.chatbot.dto.OpenAIRequest;
import com.likelion.chatbot.dto.OpenAIResponse;
import com.likelion.chatbot.entity.MessageEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    @Value("${openai.model}")
    private String model;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String chat(List<MessageEntity> messages) {
        List<OpenAIRequest.OpenAIMessage> openAIMessages = messages.stream()
                .map(msg -> OpenAIRequest.OpenAIMessage.builder()
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .build())
                .toList();

        OpenAIRequest requestBody = OpenAIRequest.builder()
                .model(model)
                .messages(openAIMessages)
                .build();

        HttpEntity<OpenAIRequest> request = new HttpEntity<>(requestBody, httpHeaders);
        OpenAIResponse response = restTemplate.postForObject(OPENAI_API_URL, request, OpenAIResponse.class);

        return response.getChoices().getFirst().getMessage().getContent();
    }
}
