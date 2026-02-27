package com.likelion.chatbot.exception;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ExceptionResponse(
        boolean success,
        ErrorDetail error
) {
    public static ExceptionResponse of(ExceptionCode code) {
        return ExceptionResponse.builder()
                .success(false)
                .error(new ErrorDetail(code.name(), code.getMessage()))
                .build();
    }

    public static ExceptionResponse of(ExceptionCode code, String message) {
        return ExceptionResponse.builder()
                .success(false)
                .error(new ErrorDetail(code.name(), message))
                .build();
    }

    public record ErrorDetail(String code, String message) {}
}
