package com.cdac.api.internal;

import com.cdac.api.internal.dto.CreatePhonePatientRequest;
import com.cdac.api.internal.dto.PhonePatientResponse;
import com.cdac.api.model.Role;
import com.cdac.api.model.User;
import com.cdac.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Registers phone callers as PATIENT accounts. Several patients may share a phone, so the phone is
 * not unique; idempotency comes from a placeholder email derived from the caller-supplied key.
 */
@Service
public class PhonePatientService {

    public record Result(PhonePatientResponse response, boolean created) {
    }

    private static final String EMAIL_DOMAIN = "@phone.medicare.invalid";

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public PhonePatientService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Result create(CreatePhonePatientRequest req) {
        String email = "p-" + sha256Hex(req.idempotencyKey()).substring(0, 24) + EMAIL_DOMAIN;

        var existing = users.findByEmail(email);
        if (existing.isPresent()) {
            return new Result(toResponse(existing.get()), false);
        }

        byte[] secret = new byte[32];
        random.nextBytes(secret);

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(Base64.getEncoder().encodeToString(secret)));
        user.setFullName(req.fullName().trim());
        user.setPhone(req.phoneE164());
        user.setRole(Role.PATIENT);
        user.setEnabled(true);
        user.setEmailVerified(false);
        return new Result(toResponse(users.save(user)), true);
    }

    private static PhonePatientResponse toResponse(User u) {
        return new PhonePatientResponse(u.getId(), u.getEmail(), u.getFullName());
    }

    private static String sha256Hex(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
