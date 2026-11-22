package com.cdac.api.internal;

import com.cdac.api.dto.ApiResponse;
import com.cdac.api.internal.dto.CreatePhonePatientRequest;
import com.cdac.api.internal.dto.PhonePatientResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Service-to-service endpoints (API-key protected), called by the phone receptionist. */
@RestController
@RequestMapping("/api/internal")
public class InternalPatientController {

    private final ApiKeyGuard guard;
    private final PhonePatientService service;

    public InternalPatientController(@Value("${app.internal.api-key:}") String apiKey,
                                     PhonePatientService service) {
        this.guard = new ApiKeyGuard(apiKey);
        this.service = service;
    }

    @PostMapping("/phone-patients")
    public ResponseEntity<ApiResponse<PhonePatientResponse>> createPhonePatient(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @Valid @RequestBody CreatePhonePatientRequest request) {
        switch (guard.check(apiKey)) {
            case DISABLED -> {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(ApiResponse.error("Internal API is disabled", null));
            }
            case UNAUTHORIZED -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or missing API key", null));
            }
            case OK -> { }
        }
        PhonePatientService.Result result = service.create(request);
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(ApiResponse.ok(result.created() ? "Patient created" : "Patient already exists",
                        result.response()));
    }
}
