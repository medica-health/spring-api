package com.cdac.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Small request bodies for the token-based auth endpoints. */
public final class TokenRequests {

    private TokenRequests() {
    }

    /** POST /api/auth/refresh and /api/auth/logout */
    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    /** POST /api/auth/forgot-password */
    public record ForgotPasswordRequest(@NotBlank @Email String email) {
    }

    /** POST /api/auth/reset-password */
    public record ResetPasswordRequest(
            @NotBlank String token,
            @NotBlank @Size(min = 8, max = 72)
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "must contain at least one letter and one digit")
            String newPassword) {
    }

    /** POST /api/auth/change-password */
    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank @Size(min = 8, max = 72)
            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "must contain at least one letter and one digit")
            String newPassword) {
    }

    /** POST /api/auth/verify-email */
    public record VerifyEmailRequest(@NotBlank String token) {
    }
}
