package com.cdac.api.internal;

import com.cdac.api.internal.dto.PhonePatientResponse;
import com.cdac.api.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = InternalPatientController.class, properties = "app.internal.api-key=k")
@Import(SecurityConfig.class)
class InternalPatientControllerTest {

    private static final String URL = "/api/internal/phone-patients";
    private static final String BODY =
            "{\"fullName\":\"Asha Rao\",\"phoneE164\":\"+919876543210\",\"idempotencyKey\":\"CA123\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PhonePatientService service;

    private PhonePatientService.Result result(boolean created) {
        return new PhonePatientService.Result(
                new PhonePatientResponse(UUID.randomUUID(), "p-abc@phone.medicare.invalid", "Asha Rao"), created);
    }

    @Test
    void missingKeyIs401() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void wrongKeyIs401() throws Exception {
        mockMvc.perform(post(URL).header("X-Api-Key", "nope")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void invalidPhoneIs400() throws Exception {
        mockMvc.perform(post(URL).header("X-Api-Key", "k")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Asha Rao\",\"phoneE164\":\"12345\",\"idempotencyKey\":\"CA123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createdIs201WithEnvelope() throws Exception {
        when(service.create(any())).thenReturn(result(true));
        mockMvc.perform(post(URL).header("X-Api-Key", "k")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(endsWith("@phone.medicare.invalid")))
                .andExpect(jsonPath("$.data.fullName").value("Asha Rao"));
    }

    @Test
    void replayIs200() throws Exception {
        when(service.create(any())).thenReturn(result(false));
        mockMvc.perform(post(URL).header("X-Api-Key", "k")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
