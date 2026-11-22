package com.cdac.api.auth.dto;

import com.cdac.api.model.Role;
import com.cdac.api.model.User;

import java.time.Instant;
import java.util.UUID;

/** Public view of an account. Never includes the password hash. */
public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String phone,
        Role role,
        boolean enabled,
        boolean emailVerified,
        Instant createdAt) {

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(), u.getRole(),
                u.isEnabled(), u.isEmailVerified(), u.getCreatedAt());
    }
}
