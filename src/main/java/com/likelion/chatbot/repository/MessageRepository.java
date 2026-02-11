package com.likelion.chatbot.repository;

import com.likelion.chatbot.entity.MessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByConversationId(Long conversationId);
    List<MessageEntity> findTop10ByConversationIdOrderByCreatedAtDesc(Long conversationId);
}
