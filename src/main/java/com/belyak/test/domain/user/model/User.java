package com.belyak.test.domain.user.model;

import com.belyak.test.domain.user.value.Email;
import com.belyak.test.domain.user.value.Password;
import com.belyak.test.domain.user.value.Username;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class User {

    private UUID id;
    private final Email email;
    private final Username username;
    private final Password password;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(UUID id, Email email, Username username, Password password, boolean active,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User createNew(Email email, Username username, Password password) {
        LocalDateTime now = LocalDateTime.now();
        return new User(null, email, username, password, true, now, now);
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }
}
