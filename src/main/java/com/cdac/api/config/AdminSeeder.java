package com.cdac.api.config;

/**
 * SKELETON. Creates the first ADMIN account on startup.
 *
 * <p>TODO: implement as an {@code ApplicationRunner} {@code @Component}:
 * <ol>
 *   <li>Read {@code app.seed-admin.email} / {@code app.seed-admin.password} ({@code ADMIN_EMAIL} /
 *       {@code ADMIN_PASSWORD}). If either is blank, do nothing.</li>
 *   <li>If {@code userRepository.existsByRole(ADMIN)}, do nothing. It must be idempotent and never
 *       overwrite an existing password.</li>
 *   <li>Otherwise create the user with role ADMIN, enabled and email_verified, a BCrypt-hashed
 *       password and full_name "Platform Admin", then log
 *       {@code "Seeded initial admin <email>"}. Never log the password.</li>
 * </ol>
 * Runs after Flyway, so auth.users exists.
 */
public class AdminSeeder {
}
