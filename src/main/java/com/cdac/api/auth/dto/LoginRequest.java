package com.cdac.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** POST /api/auth/login */
public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
}
