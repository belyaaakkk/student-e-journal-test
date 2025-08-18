package com.belyak.test.user.domain;

import lombok.Getter;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user in the system with email, username, and other attributes.
 */
@Getter
public class User extends Entity<UUID> {

    private final Email email;
    private Username username;
    private final String password;
    private boolean active;
    /**
     * -- SETTER --
     *  Sets the user's Telegram ID.
     *
     * @param telegramId the Telegram ID (nullable)
     */
    @Setter
    private String telegramId;

    /**
     * Creates a user instance for repository mapping.
     *
     * @param id          the user ID
     * @param email       the user's email
     * @param username    the user's username
     * @param password    the user's password (hashed)
     * @param active      whether the user is active
     * @param telegramId  the user's Telegram ID (nullable)
     * @param createdAt   the creation timestamp
     * @param updatedAt   the last update timestamp
     */
    public User(UUID id, Email email, Username username, String password,
                boolean active, String telegramId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.password = BCrypt.hashpw(
                Objects.requireNonNull(password, "Password cannot be null"),
                BCrypt.gensalt()
        );
        this.active = active;
        this.telegramId = telegramId;
    }

    /**
     * Checks if the provided password matches the user's password.
     *
     * @param password the password to check
     * @return true if the password matches, false otherwise
     */
    public boolean matchesPassword(String password) {
        return password != null && BCrypt.checkpw(password, this.password);
    }

    /**
     * Updates the user's username.
     *
     * @param newUsername the new username
     */
    public void updateUsername(Username newUsername) {
        this.username = Objects.requireNonNull(newUsername, "New username cannot be null");
    }

    /**
     * Activates the user.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Deactivates the user.
     */
    public void deactivate() {
        this.active = false;
    }

}