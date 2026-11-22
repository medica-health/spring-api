package com.cdac.api.model;

/**
 * Application roles. The name is stored in auth.users.role and emitted as the JWT
 * {@code role} claim, which express-api uses for RBAC. Keep in sync with
 * express-api src/types/auth.ts.
 */
public enum Role {
    PATIENT,
    DOCTOR,
    RECEPTIONIST,
    ADMIN
}
