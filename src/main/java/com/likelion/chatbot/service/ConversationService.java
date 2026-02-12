package com.likelion.chatbot.service;

import com.likelion.chatbot.dto.ConversationResponse;
import com.likelion.chatbot.dto.MessageResponse;
import com.likelion.chatbot.entity.ConversationEntity;
import com.likelion.chatbot.entity.MessageEntity;
import com.likelion.chatbot.repository.ConversationRepository;
import com.likelion.chatbot.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public List<ConversationResponse> getConversations() {
        return conversationRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(ConversationResponse::from)
                .toList();
    }

    @Transactional
    public List<MessageResponse> getMessages(Long conversationId) {
        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다. id = " + conversationId));
        return messageRepository.findByConversationId(conversationId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Transactional
    public void deleteConversation(Long id) {
        ConversationEntity conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화입니다. id = " + id));
        List<MessageEntity> messages = messageRepository.findByConversationId(id);
        messageRepository.deleteAll(messages);
        conversationRepository.delete(conversation);
    }
}
