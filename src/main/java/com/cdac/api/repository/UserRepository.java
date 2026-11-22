package com.cdac.api.repository;

import com.cdac.api.model.Role;
import com.cdac.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    /** Pass a lower-cased email: auth.users.email is stored lower-case. */
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    Page<User> findByRole(Role role, Pageable pageable);
}
