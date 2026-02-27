package com.likelion.chatbot.service;

import com.likelion.chatbot.dto.ConversationResponse;
import com.likelion.chatbot.exception.ExceptionCode;
import com.likelion.chatbot.dto.MessageResponse;
import com.likelion.chatbot.entity.ConversationEntity;
import com.likelion.chatbot.entity.MessageEntity;
import com.likelion.chatbot.entity.UserEntity;
import com.likelion.chatbot.exception.ChatbotException;
import com.likelion.chatbot.repository.ConversationRepository;
import com.likelion.chatbot.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public List<ConversationResponse> getConversations() {
        UserEntity currentUser = getCurrentUser();
        return conversationRepository.findAllByUserOrderByUpdatedAtDesc(currentUser)
                .stream()
                .map(ConversationResponse::from)
                .toList();
    }

    @Transactional
    public List<MessageResponse> getMessages(Long conversationId) {
        UserEntity currentUser = getCurrentUser();
        conversationRepository.findByIdAndUser(conversationId, currentUser)
                .orElseThrow(() -> new ChatbotException(ExceptionCode.NOT_FOUND));
        return messageRepository.findByConversationId(conversationId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Transactional
    public void deleteConversation(Long id) {
        UserEntity currentUser = getCurrentUser();
        ConversationEntity conversation = conversationRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ChatbotException(ExceptionCode.NOT_FOUND));
        List<MessageEntity> messages = messageRepository.findByConversationId(id);
        messageRepository.deleteAll(messages);
        conversationRepository.delete(conversation);
    }

    private UserEntity getCurrentUser() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
