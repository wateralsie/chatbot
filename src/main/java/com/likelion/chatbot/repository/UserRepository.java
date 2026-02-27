package com.likelion.chatbot.repository;

import com.likelion.chatbot.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByApiKey(String apiKey);
    boolean existsByEmail(String email);
}
