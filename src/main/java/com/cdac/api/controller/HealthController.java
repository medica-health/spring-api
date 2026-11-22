package com.cdac.api.controller;

import com.cdac.api.dto.ApiResponse;
import com.cdac.api.service.DatabaseStatusService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final String environment;
    private final DatabaseStatusService databaseStatusService;

    public HealthController(@Value("${spring.profiles.active:development}") String environment,
                            DatabaseStatusService databaseStatusService) {
        this.environment = environment;
        this.databaseStatusService = databaseStatusService;
    }

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of("message", "Running");
    }

    @GetMapping("/api/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("environment", environment);
        data.put("databases", databaseStatusService.status());
        return ApiResponse.ok("API is running successfully", data);
    }
}
