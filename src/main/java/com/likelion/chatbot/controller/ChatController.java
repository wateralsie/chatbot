package com.likelion.chatbot.controller;

import com.likelion.chatbot.dto.BaseResponse;
import com.likelion.chatbot.dto.ChatRequest;
import com.likelion.chatbot.dto.ChatResponse;
import com.likelion.chatbot.dto.ChatStreamingResponse;
import com.likelion.chatbot.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Chat", description = "채팅 API")
@SecurityRequirement(name = "ApiKey")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅 메시지 전송", description = "사용자 메시지를 전송하고 AI 응답을 받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청값"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/completions")
    public ResponseEntity<BaseResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request);
        return ResponseEntity.ok(BaseResponse.of(response));
    }

    @Operation(summary = "채팅 스트리밍", description = "사용자 메시지를 전송하고 AI 응답을 SSE 스트림으로 받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공 (SSE 스트림)"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청값"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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
