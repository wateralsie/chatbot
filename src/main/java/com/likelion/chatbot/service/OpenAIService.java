package com.likelion.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.chatbot.dto.ChatRequest;
import com.likelion.chatbot.dto.OpenAIRequest;
import com.likelion.chatbot.dto.OpenAIResponse;
import com.likelion.chatbot.dto.OpenAIStreamingResponse;
import com.likelion.chatbot.entity.MessageEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

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

    public Flux<String> chatStreaming(List<MessageEntity> messages) {
        List<OpenAIRequest.OpenAIMessage> openAIMessages = messages.stream()
                .map(msg -> OpenAIRequest.OpenAIMessage.builder()
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .build())
                .toList();

        OpenAIRequest requestBody = OpenAIRequest.builder()
                .model(model)
                .messages(openAIMessages)
                .stream(true)
                .build();

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(line -> !line.isBlank() && !line.equals("[DONE]"))
                .mapNotNull(line -> {
                    try  {
                        OpenAIStreamingResponse chunk = objectMapper.readValue(line, OpenAIStreamingResponse.class);
                        OpenAIStreamingResponse.Delta delta = chunk.getChoices().getFirst().getDelta();
                        if (delta != null) {
                            return delta.getContent();
                        }
                        return null;
                    } catch (JsonProcessingException e) {
                        // TODO: 에러 처리
                        return null;
                    }
                });
    }
}
