package com.likelion.chatbot.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record BaseResponse<T>(
        boolean success,
        T data
) {
    public static <T> BaseResponse<T> of(T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
}
