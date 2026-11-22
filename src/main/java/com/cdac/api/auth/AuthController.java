package com.cdac.api.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication endpoints under {@code /api/auth}. SKELETON: each endpoint is documented
 * below and delegates to {@link AuthService} once implemented. All responses use
 * {@code ApiResponse} ({@code { success, message, data, timestamp }}).
 *
 * <p>The Next.js server (BFF) calls these endpoints and keeps both tokens in httpOnly
 * cookies on its own domain, so tokens are returned in the JSON body, not as cookies.
 *
 * <h2>POST /api/auth/register</h2>
 * Body {@link com.cdac.api.auth.dto.RegisterRequest}: {@code fullName, email, phone?, password, role}.
 * <ul>
 *   <li>{@code role} may only be {@code PATIENT} or {@code DOCTOR}. RECEPTIONIST and ADMIN accounts
 *       are created by an admin. Anything else returns 400.</li>
 *   <li>Password policy: 8–72 chars (the BCrypt limit), at least one letter and one digit.</li>
 *   <li>Email already registered returns {@code 409 "An account with this email already exists"}.</li>
 *   <li>Creates the user (enabled, email_verified=false), sends a verification email (24h token),
 *       and returns {@code 201} with {@link com.cdac.api.auth.dto.AuthResponse}, so the user is
 *       logged in immediately.</li>
 *   <li>Doctors start PENDING in express-api (app.doctor_profiles is created on first use) and
 *       aren't searchable until an admin approves them.</li>
 * </ul>
 *
 * <h2>POST /api/auth/login</h2>
 * Body {@link com.cdac.api.auth.dto.LoginRequest}: {@code email, password}.
 * <ul>
 *   <li>Wrong email, wrong password and disabled account all return the same
 *       {@code 401 "Invalid email or password"}, so accounts can't be enumerated.</li>
 *   <li>Rate limit: 5 failures per email and 20 per IP per 15 minutes, then {@code 429}.</li>
 *   <li>On success: update last_login_at, create a refresh token (new family_id), and return
 *       {@code 200} {@link com.cdac.api.auth.dto.AuthResponse}.</li>
 * </ul>
 *
 * <h2>POST /api/auth/refresh</h2>
 * Body {@code { refreshToken }}.
 * <ul>
 *   <li>Look up by SHA-256 hash. Unknown or expired tokens return {@code 401}.</li>
 *   <li><b>Rotation:</b> revoke the presented token, issue a new one in the same family, and set
 *       {@code replaced_by}. Return a new access token and refresh token.</li>
 *   <li><b>Reuse detection:</b> if the presented token is already revoked, revoke the whole family
 *       and return {@code 401}. That user is logged out on every device.</li>
 *   <li>A disabled user returns {@code 401}.</li>
 * </ul>
 *
 * <h2>POST /api/auth/logout</h2>
 * Body {@code { refreshToken }}: revokes that token. Always returns {@code 200}, even if the
 * token is unknown. Access tokens are short-lived and simply expire.
 *
 * <h2>GET /api/auth/me</h2>
 * Requires a Bearer access token. Returns {@link com.cdac.api.auth.dto.UserResponse} for {@code sub}.
 *
 * <h2>POST /api/auth/change-password</h2>
 * Requires a Bearer token. Body {@code { currentPassword, newPassword }}.
 * A wrong current password returns {@code 400}. On success it revokes all refresh tokens and
 * returns {@code 200}. The client then logs in again.
 *
 * <h2>POST /api/auth/forgot-password</h2>
 * Body {@code { email }}. Always returns {@code 200 "If the account exists, a reset link has been sent"}.
 * If the account exists, store a 30-minute reset token and email
 * {@code {APP_BASE_URL}/reset-password?token=...}.
 *
 * <h2>POST /api/auth/reset-password</h2>
 * Body {@code { token, newPassword }}. An invalid, expired or used token returns {@code 400}.
 * On success it sets the password, marks the token used and revokes all refresh tokens.
 *
 * <h2>POST /api/auth/verify-email</h2>
 * Body {@code { token }}. Sets email_verified=true and marks the token used. An invalid token
 * returns {@code 400}. Also: {@code POST /api/auth/resend-verification} (authenticated),
 * rate limited to 1 per minute.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // TODO: inject AuthService and add the handlers documented above.
}
