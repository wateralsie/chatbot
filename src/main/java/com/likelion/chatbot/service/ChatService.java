package com.likelion.chatbot.service;

import com.likelion.chatbot.dto.ChatRequest;
import com.likelion.chatbot.dto.ChatResponse;
import com.likelion.chatbot.dto.ChatStreamingResponse;
import com.likelion.chatbot.entity.ConversationEntity;
import com.likelion.chatbot.entity.MessageEntity;
import com.likelion.chatbot.repository.ConversationRepository;
import com.likelion.chatbot.repository.MessageRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OpenAIService openAIService;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final WebClient webClient;

    @Transactional
    public ChatResponse chat(ChatRequest request) {
        // 1. 대화 조회 또는 생성
        ConversationEntity conversation;
        if (request.getConversationId() != null) {
            conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다. id = " + request.getConversationId()));
        } else {
            conversation = ConversationEntity.builder()
                    .title(request.getMessage().substring(0, Math.min(50, request.getMessage().length())))
                    .build();
            conversationRepository.save(conversation);
        }

        // 2. 사용자 메시지 저장
        MessageEntity userMessage = MessageEntity.builder()
                .conversation(conversation)
                .role("user")
                .content(request.getMessage())
                .build();
        messageRepository.save(userMessage);

        // 3. 이전 대화 컨텍스트 조회 (최근 10개)
        List<MessageEntity> contextMessages = messageRepository
                .findTop10ByConversationIdOrderByCreatedAtDesc(conversation.getId());
        Collections.reverse(contextMessages);

        // 4. OpenAI API 호출
        String aiResponse = openAIService.chat(contextMessages);

        // 5. AI 응답 저장
        MessageEntity assistantMessage = MessageEntity.builder()
                .conversation(conversation)
                .role("assistant")
                .content(aiResponse)
                .build();
        messageRepository.save(assistantMessage);

        // 5. 응답 반환
        return ChatResponse.builder()
                .conversationId(conversation.getId())
                .answer(aiResponse)
                .build();
    }

    public Flux<ChatStreamingResponse> chatStreaming(ChatRequest request) {
        // 1. 대화 조회 또는 생성
        ConversationEntity conversation;
        if (request.getConversationId() != null) {
            conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다. id = " + request.getConversationId()));
        } else {
            conversation = ConversationEntity.builder()
                    .title(request.getMessage().substring(0, Math.min(50, request.getMessage().length())))
                    .build();
            conversationRepository.save(conversation);
        }

        // 2. 사용자 메시지 저장
        MessageEntity userMessage = MessageEntity.builder()
                .conversation(conversation)
                .role("user")
                .content(request.getMessage())
                .build();
        messageRepository.save(userMessage);

        // 3. 이전 대화 컨텍스트 조회 (최근 10개)
        List<MessageEntity> contextMessages = messageRepository
                .findTop10ByConversationIdOrderByCreatedAtDesc(conversation.getId());
        Collections.reverse(contextMessages);

        Long conversationsId = conversation.getId();
        StringBuilder fullResponse = new StringBuilder();

        return openAIService.chatStreaming(contextMessages)
                .map(token -> {
                    fullResponse.append(token);
                    return ChatStreamingResponse.builder()
                            .conversationId(conversationsId)
                            .answer(token)
                            .done(false)
                            .build();
                })
                .concatWithValues(
                        ChatStreamingResponse.builder()
                                .conversationId(conversationsId)
                                .answer(null)
                                .done(true)
                                .build()
                )
                .doOnComplete(() -> {
                    // 스트리밍 완료시 ai 응답 db에 저장
                    Mono.fromRunnable(() -> {
                        MessageEntity assistantMessage = MessageEntity.builder()
                                .conversation(conversationRepository.findById(conversationsId).orElseThrow())
                                .role("assistant")
                                .content(fullResponse.toString())
                                .build();
                        messageRepository.save(assistantMessage);
                    })
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
                });
    }
}
