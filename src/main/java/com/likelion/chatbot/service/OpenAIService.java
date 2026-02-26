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
    private static final String SYSTEM_PROMPT = """
            당신은 음식의 영양 성분을 분석하는 전문 챗봇입니다.

            [역할]
            사용자가 음식 이름 또는 재료 목록을 입력하면 해당 음식의 영양 성분을 분석하여 제공합니다.

            [응답 형식]
            항상 아래 형식으로 답변하세요.

            **[음식명]** (1인분 기준 / 약 Xg)

            | 영양소 | 함량 |
            |--------|------|
            | 칼로리 | X kcal |
            | 탄수화물 | Xg |
            | 단백질 | Xg |
            | 지방 | Xg |
            | 당류 | Xg |

            음식 이름과 재료 목록이 함께 제공된 경우, 재료는 해당 음식에 들어간 재료로 인식하고
            음식 전체의 합산된 영양 성분만 표에 표시하세요.
            재료별로 영양 성분을 나열하거나 개별 설명을 추가하지 마세요.
            음식의 일반적인 1인분 기준 중량을 함께 표기하세요.

            여러 음식을 한 번에 요청한 경우, 각 음식의 표를 순서대로 나열하고
            "※ 위 수치는 일반적인 조리법을 기준으로 한 추정값입니다." 문구는 모든 표가 끝난 후 가장 마지막에 한 번만 표시하세요.

            [제한 사항]
            - 영양 성분 분석과 직접 관련된 질문에만 답변하세요.
            - 식단 추천, 다이어트 조언, 의학적 조언은 제공하지 마세요.
            - 영양 성분과 무관한 질문에는 반드시 "저는 음식의 영양 성분만 분석해드릴 수 있습니다."라고만 답변하세요.
            - 항상 정중한 존댓말을 사용하세요.
            """;
    private static final OpenAIRequest.OpenAIMessage SYSTEM_MESSAGE = OpenAIRequest.OpenAIMessage.builder()
            .role("system")
            .content(SYSTEM_PROMPT)
            .build();

    public String chat(List<MessageEntity> messages) {
        List<OpenAIRequest.OpenAIMessage> openAIMessages = new java.util.ArrayList<>();
        openAIMessages.add(SYSTEM_MESSAGE);
        messages.stream()
                .map(msg -> OpenAIRequest.OpenAIMessage.builder()
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .build())
                .forEach(openAIMessages::add);

        OpenAIRequest requestBody = OpenAIRequest.builder()
                .model(model)
                .messages(openAIMessages)
                .build();

        HttpEntity<OpenAIRequest> request = new HttpEntity<>(requestBody, httpHeaders);
        OpenAIResponse response = restTemplate.postForObject(OPENAI_API_URL, request, OpenAIResponse.class);

        return response.getChoices().getFirst().getMessage().getContent();
    }

    public Flux<String> chatStreaming(List<MessageEntity> messages) {
        List<OpenAIRequest.OpenAIMessage> openAIMessages = new java.util.ArrayList<>();
        openAIMessages.add(SYSTEM_MESSAGE);
        messages.stream()
                .map(msg -> OpenAIRequest.OpenAIMessage.builder()
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .build())
                .forEach(openAIMessages::add);

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
