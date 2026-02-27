package com.likelion.chatbot.controller;

import com.likelion.chatbot.dto.BaseResponse;
import com.likelion.chatbot.dto.ChatRequest;
import com.likelion.chatbot.dto.ChatResponse;
import com.likelion.chatbot.dto.ChatStreamingResponse;
import com.likelion.chatbot.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/completions")
    public ResponseEntity<BaseResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request);
        return ResponseEntity.ok(BaseResponse.of(response));
    }

    @PostMapping(value = "/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatStreamingResponse>> chatStreaming(
            @Valid @RequestBody ChatRequest request
    ) {
        return chatService.chatStreaming(request)
                .map(data -> ServerSentEvent.<ChatStreamingResponse>builder()
                        .data(data)
                        .build());
    }
}
