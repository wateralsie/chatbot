package com.likelion.chatbot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "유효하지 않은 API Key입니다."),
    NOT_FOUND(404, "존재하지 않습니다."),

    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(503, "AI 서비스에 연결할 수 없습니다."),
    ;

    private final int statusCode;
    private final String message;
}
