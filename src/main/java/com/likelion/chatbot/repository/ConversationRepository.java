package com.likelion.chatbot.repository;

import com.likelion.chatbot.entity.ConversationEntity;
import com.likelion.chatbot.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
    List<ConversationEntity> findAllByUserOrderByUpdatedAtDesc(UserEntity user);
    Optional<ConversationEntity> findByIdAndUser(Long id, UserEntity user);
}
