package com.cdac.api.internal;

import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Guards /api/internal/** with the API-key interceptor. */
@Configuration
public class InternalApiConfig implements WebMvcConfigurer {

    private final ApiKeyInterceptor interceptor;

    public InternalApiConfig(@Value("${app.internal.api-key:}") String apiKey, ObjectMapper mapper) {
        this.interceptor = new ApiKeyInterceptor(new ApiKeyGuard(apiKey), mapper);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/api/internal/**");
    }
}
