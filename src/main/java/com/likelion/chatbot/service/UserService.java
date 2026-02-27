package com.likelion.chatbot.service;

import com.likelion.chatbot.dto.LoginRequest;
import com.likelion.chatbot.dto.SignupRequest;
import com.likelion.chatbot.dto.SignupResponse;
import com.likelion.chatbot.entity.UserEntity;
import com.likelion.chatbot.exception.ChatbotException;
import com.likelion.chatbot.exception.ExceptionCode;
import com.likelion.chatbot.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ChatbotException(ExceptionCode.BAD_REQUEST);
        }

        String apiKey = UUID.randomUUID().toString();

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .apiKey(apiKey)
                .build();
        userRepository.save(user);

        return SignupResponse.builder()
                .email(user.getEmail())
                .apiKey(apiKey)
                .build();
    }

    @Transactional(readOnly = true)
    public SignupResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ChatbotException(ExceptionCode.NOT_FOUND));

        return SignupResponse.builder()
                .email(user.getEmail())
                .apiKey(user.getApiKey())
                .build();
    }
}
