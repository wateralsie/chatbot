package com.likelion.chatbot.controller;

import com.likelion.chatbot.dto.BaseResponse;
import com.likelion.chatbot.dto.ConversationResponse;
import com.likelion.chatbot.dto.MessageResponse;
import com.likelion.chatbot.service.ConversationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ConversationResponse>>> getConversations() {
        return ResponseEntity.ok(BaseResponse.of(conversationService.getConversations()));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<BaseResponse<List<MessageResponse>>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.of(conversationService.getMessages(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteConversation(@PathVariable Long id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.ok(BaseResponse.of("대화가 삭제되었습니다."));
    }
}
