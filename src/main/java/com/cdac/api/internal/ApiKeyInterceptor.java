package com.cdac.api.internal;

import com.cdac.api.dto.ApiResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/** Rejects /api/internal/** calls without a valid X-Api-Key, before body binding, as an ApiResponse envelope. */
public class ApiKeyInterceptor implements HandlerInterceptor {

    private final ApiKeyGuard guard;
    private final ObjectMapper mapper;

    public ApiKeyInterceptor(ApiKeyGuard guard, ObjectMapper mapper) {
        this.guard = guard;
        this.mapper = mapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        return switch (guard.check(request.getHeader("X-Api-Key"))) {
            case OK -> true;
            case DISABLED -> reject(response, HttpStatus.SERVICE_UNAVAILABLE, "Internal API is disabled");
            case UNAUTHORIZED -> reject(response, HttpStatus.UNAUTHORIZED, "Invalid or missing API key");
        };
    }

    private boolean reject(HttpServletResponse response, HttpStatus status, String message) throws Exception {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        mapper.writeValue(response.getOutputStream(), ApiResponse.error(message, null));
        return false;
    }
}
