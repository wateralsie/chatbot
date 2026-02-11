package com.likelion.chatbot.service;

import com.likelion.chatbot.dto.ChatRequest;
import com.likelion.chatbot.dto.ChatResponse;
import com.likelion.chatbot.dto.ConversationResponse;
import com.likelion.chatbot.dto.MessageResponse;
import com.likelion.chatbot.entity.ConversationEntity;
import com.likelion.chatbot.entity.MessageEntity;
import com.likelion.chatbot.repository.ConversationRepository;
import com.likelion.chatbot.repository.MessageRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OpenAIService openAIService;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

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
}
