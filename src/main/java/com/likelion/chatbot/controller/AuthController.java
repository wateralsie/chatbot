package com.likelion.chatbot.controller;

import com.likelion.chatbot.dto.BaseResponse;
import com.likelion.chatbot.dto.LoginRequest;
import com.likelion.chatbot.dto.SignupRequest;
import com.likelion.chatbot.dto.SignupResponse;
import com.likelion.chatbot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새 계정을 생성하고 API Key를 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청값"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.of(userService.signup(request)));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 API Key를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청값"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<SignupResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(BaseResponse.of(userService.login(request)));
    }
}
