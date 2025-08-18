package com.belyak.test.domain.user.repository;

import com.belyak.test.domain.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void delete(User user);

    Optional<User> findById(UUID id);
}
