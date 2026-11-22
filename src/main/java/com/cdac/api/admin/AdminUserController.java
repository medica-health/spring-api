package com.cdac.api.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account administration under {@code /api/admin/users}. Every endpoint requires
 * {@code ROLE_ADMIN} (SecurityConfig). SKELETON: endpoints are documented only.
 *
 * <p>Domain administration (doctor verification, specializations, receptionist ↔ doctor
 * assignments, stats) lives in express-api under {@code /api/admin/*}. This controller only
 * manages identities.
 *
 * <h2>GET /api/admin/users?role=&amp;q=&amp;enabled=&amp;page=&amp;size=</h2>
 * Paged list of {@code UserResponse}, filtered by role, by name or email ({@code q}, case-insensitive
 * contains) and by enabled. Sorted by createdAt desc.
 *
 * <h2>GET /api/admin/users/{id}</h2>
 * One user. Returns 404 if missing.
 *
 * <h2>POST /api/admin/users</h2>
 * Body: {@code fullName, email, phone?, role, password?}. Creates staff accounts (RECEPTIONIST,
 * ADMIN), and DOCTOR or PATIENT on someone's behalf.
 * <ul>
 *   <li>Duplicate email returns 409.</li>
 *   <li>If {@code password} is omitted, generate a random one and email a reset-password link
 *       (a 30-minute token) instead of returning a password.</li>
 *   <li>The account is created with email_verified=true, because an admin vouched for it.</li>
 *   <li>Returns 201 {@code UserResponse}.</li>
 * </ul>
 *
 * <h2>PATCH /api/admin/users/{id}</h2>
 * Body: {@code { role?, enabled?, fullName?, phone? }}.
 * <ul>
 *   <li>Disabling a user or changing their role revokes all of their refresh tokens, so the change
 *       takes effect within one access-token lifetime (15 min).</li>
 *   <li>An admin can't disable or demote themselves, and the last enabled ADMIN can't be removed.
 *       Both return 409.</li>
 * </ul>
 *
 * <h2>POST /api/admin/users/{id}/revoke-sessions</h2>
 * Revokes all of the user's refresh tokens ("log out everywhere").
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    // TODO: inject an AdminUserService (UserRepository, RefreshTokenRepository, PasswordEncoder,
    //       mail) and implement the endpoints documented above.
}
