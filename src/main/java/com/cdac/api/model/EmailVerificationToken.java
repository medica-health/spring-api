package com.cdac.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

/** auth.email_verification_tokens: emailed after registration, valid for 24 hours. */
@Entity
@Table(name = "email_verification_tokens", schema = "auth")
@NoArgsConstructor
public class EmailVerificationToken extends OneTimeToken {
}
