package com.cdac.api.internal.dto;

import java.util.UUID;

public record PhonePatientResponse(UUID id, String email, String fullName) {
}
