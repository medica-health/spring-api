package com.cdac.api.internal;

import com.cdac.api.internal.dto.CreatePhonePatientRequest;
import com.cdac.api.model.Role;
import com.cdac.api.model.User;
import com.cdac.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PhonePatientServiceTest {

    private UserRepository users;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private PhonePatientService service;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        service = new PhonePatientService(users, encoder);
        when(users.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
    }

    @Test
    void createsPatientWithPlaceholderEmailAndUnusablePassword() {
        when(users.findByEmail(any())).thenReturn(Optional.empty());

        var result = service.create(new CreatePhonePatientRequest("Asha Rao", "+919876543210", "CA123"));

        assertThat(result.created()).isTrue();
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getRole()).isEqualTo(Role.PATIENT);
        assertThat(saved.getPhone()).isEqualTo("+919876543210");
        assertThat(saved.getFullName()).isEqualTo("Asha Rao");
        assertThat(saved.isEmailVerified()).isFalse();
        assertThat(saved.getEmail()).matches("p-[0-9a-f]{24}@phone\\.medicare\\.invalid");
        assertThat(saved.getPasswordHash()).isNotNull();
        assertThat(encoder.matches("", saved.getPasswordHash())).isFalse();
        assertThat(result.response().email()).isEqualTo(saved.getEmail());
    }

    @Test
    void sameIdempotencyKeyReturnsExistingWithoutSaving() {
        User existing = new User();
        existing.setId(UUID.randomUUID());
        existing.setEmail("p-x@phone.medicare.invalid");
        existing.setFullName("Asha Rao");
        when(users.findByEmail(any())).thenReturn(Optional.of(existing));

        var result = service.create(new CreatePhonePatientRequest("Asha Rao", "+919876543210", "CA123"));

        assertThat(result.created()).isFalse();
        assertThat(result.response().id()).isEqualTo(existing.getId());
        verify(users, never()).save(any());
    }

    @Test
    void existingPhoneIsNotAConflict() {
        // Another account on the same phone has a different email, so findByEmail misses.
        when(users.findByEmail(any())).thenReturn(Optional.empty());

        var result = service.create(new CreatePhonePatientRequest("Other Person", "+919876543210", "CA999"));

        assertThat(result.created()).isTrue();
        verify(users).save(any(User.class));
    }
}
