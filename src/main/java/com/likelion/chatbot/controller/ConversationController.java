package com.likelion.chatbot.controller;

import com.likelion.chatbot.dto.BaseResponse;
import com.likelion.chatbot.dto.ConversationResponse;
import com.likelion.chatbot.dto.MessageResponse;
import com.likelion.chatbot.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Conversation", description = "대화 관리 API")
@SecurityRequirement(name = "ApiKey")
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(summary = "대화 목록 조회", description = "내 모든 대화 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<BaseResponse<List<ConversationResponse>>> getConversations() {
        return ResponseEntity.ok(BaseResponse.of(conversationService.getConversations()));
    }

    @Operation(summary = "대화 메시지 조회", description = "특정 대화의 메시지 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "대화를 찾을 수 없음")
    })
    @GetMapping("/{id}/messages")
    public ResponseEntity<BaseResponse<List<MessageResponse>>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.of(conversationService.getMessages(id)));
    }

    @Operation(summary = "대화 삭제", description = "특정 대화와 모든 메시지를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "대화를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteConversation(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.ok(BaseResponse.of("대화가 삭제되었습니다."));
    }
}
