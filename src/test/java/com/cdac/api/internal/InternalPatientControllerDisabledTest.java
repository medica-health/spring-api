package com.cdac.api.internal;

import com.cdac.api.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = InternalPatientController.class, properties = "app.internal.api-key=")
@Import(SecurityConfig.class)
class InternalPatientControllerDisabledTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PhonePatientService service;

    @Test
    void disabledIs503() throws Exception {
        mockMvc.perform(post("/api/internal/phone-patients").header("X-Api-Key", "anything")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Asha Rao\",\"phoneE164\":\"+919876543210\",\"idempotencyKey\":\"CA123\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success").value(false));
    }
}
