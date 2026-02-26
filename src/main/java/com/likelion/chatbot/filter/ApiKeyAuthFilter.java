package com.likelion.chatbot.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.chatbot.dto.ExceptionResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER_NAME = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Value("${app.api-key}")
    private String validApiKey;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(TOKEN_HEADER_NAME);

        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            sendUnauthorized(response, "API Key가 없습니다.");
            return;
        }

        String apiKey = authHeader.substring(TOKEN_PREFIX.length());

        if (!validApiKey.equals(apiKey)) {
            sendUnauthorized(response, "유효하지 않은 API Key 입니다.");
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(apiKey, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new ExceptionResponse(401, message)));
    }
}
