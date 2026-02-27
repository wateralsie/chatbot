package com.likelion.chatbot.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ExceptionResponse(
        boolean success,
        ErrorDetail error
) {
    public static ExceptionResponse of(String code, String message) {
        return ExceptionResponse.builder()
                .success(false)
                .error(new ErrorDetail(code, message))
                .build();
    }

    public record ErrorDetail(String code, String message) {}
}
