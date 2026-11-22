package com.cdac.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

/** auth.password_reset_tokens: emailed by /api/auth/forgot-password, valid for 30 minutes. */
@Entity
@Table(name = "password_reset_tokens", schema = "auth")
@NoArgsConstructor
public class PasswordResetToken extends OneTimeToken {
}
