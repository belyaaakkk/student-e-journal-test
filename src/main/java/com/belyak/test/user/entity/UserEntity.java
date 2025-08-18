package com.belyak.test.user.entity;

import com.belyak.test.team.entity.TeamMembershipEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * JPA entity representing a user, now implementing UserDetails for Spring Security.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_active", columnList = "active"),
        @Index(name = "idx_users_telegram_id", columnList = "telegram_id")
})
public class UserEntity extends BasicEntity implements UserDetails {

    @Setter
    @Getter
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email"))
    private Email email;

    @Setter
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "username"))
    private Username username;

    @Getter
    @NotNull
    @Column(name = "password", nullable = false)
    @Size(min = 6, max = 255)
    private String password;

    @Getter
    @Setter
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Setter
    @Getter
    @Column(name = "telegram_id", unique = true)
    private String telegramId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TeamMembershipEntity> teamMembershipEntities = new HashSet<>();

    protected UserEntity() {
    }

    public UserEntity(Email email, Username username, String password) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.password = BCrypt.hashpw(
                Objects.requireNonNull(password, "Password cannot be null"),
                BCrypt.gensalt()
        );
    }

    /**
     * Updates the user's password.
     *
     * @param newPassword the new password
     */
    public void setPassword(String newPassword) {
        this.password = BCrypt.hashpw(
                Objects.requireNonNull(newPassword, "New password cannot be null"),
                BCrypt.gensalt()
        );
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
     * Deactivates the user.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Activates the user.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Returns an unmodifiable set of team memberships.
     *
     * @return the set of team memberships
     */
    public Set<TeamMembershipEntity> getTeamMembershipEntities() {
        return Collections.unmodifiableSet(teamMembershipEntities);
    }

    // UserDetails implementations
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Or add default "ROLE_USER" if needed: List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email.value(); // Use email as username for authentication
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}