package com.cdac.api.auth.dto;

/**
 * Returned by register, login and refresh.
 *
 * @param accessToken      HS256 JWT (see JwtService for the claim contract)
 * @param accessExpiresIn  seconds until the access token expires
 * @param refreshToken     opaque token, present only in this response
 * @param refreshExpiresIn seconds until the refresh token expires
 * @param user             the authenticated user
 */
public record AuthResponse(
        String accessToken,
        long accessExpiresIn,
        String refreshToken,
        long refreshExpiresIn,
        UserResponse user) {
}
