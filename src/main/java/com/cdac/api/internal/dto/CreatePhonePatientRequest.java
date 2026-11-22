package com.cdac.api.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePhonePatientRequest(
        @NotBlank @Size(min = 2, max = 150) String fullName,
        @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "must be E.164, e.g. +919876543210") String phoneE164,
        @NotBlank @Size(min = 1, max = 100) String idempotencyKey) {
}
