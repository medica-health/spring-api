package com.cdac.api.internal;

import com.cdac.api.dto.ApiResponse;
import com.cdac.api.internal.dto.CreatePhonePatientRequest;
import com.cdac.api.internal.dto.PhonePatientResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Service-to-service endpoints, API-key protected by {@link InternalApiConfig}'s interceptor. */
@RestController
@RequestMapping("/api/internal")
public class InternalPatientController {

    private final PhonePatientService service;

    public InternalPatientController(PhonePatientService service) {
        this.service = service;
    }

    @PostMapping("/phone-patients")
    public ResponseEntity<ApiResponse<PhonePatientResponse>> createPhonePatient(
            @Valid @RequestBody CreatePhonePatientRequest request) {
        PhonePatientService.Result result = service.create(request);
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(ApiResponse.ok(result.created() ? "Patient created" : "Patient already exists",
                        result.response()));
    }
}
