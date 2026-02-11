package com.likelion.chatbot.repository;

import com.likelion.chatbot.entity.ConversationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
    List<ConversationEntity> findAllByOrderByUpdatedAtDesc();
}
