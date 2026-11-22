package com.cdac.api.controller;

import com.cdac.api.security.SecurityConfig;
import com.cdac.api.service.DatabaseStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
@Import(SecurityConfig.class) // use the app's filter chain, not Boot's default (which would 401)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DatabaseStatusService databaseStatusService;

    @Test
    void healthReportsBothDatabases() throws Exception {
        when(databaseStatusService.status()).thenReturn(Map.of("postgres", "up", "mongodb", "down"));

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.environment").exists())
                .andExpect(jsonPath("$.data.databases.postgres").value("up"))
                .andExpect(jsonPath("$.data.databases.mongodb").value("down"));
    }

    @Test
    void unknownRouteReturns404Envelope() throws Exception {
        mockMvc.perform(get("/api/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
