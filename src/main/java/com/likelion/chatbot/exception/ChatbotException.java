package com.likelion.chatbot.exception;

public class ChatbotException extends RuntimeException {

    public final ExceptionCode exceptionCode;

    public ChatbotException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
