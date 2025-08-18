package com.belyak.test.team.domain;

import com.belyak.test.user.domain.Entity;
import com.belyak.test.user.domain.Role;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a membership of a user in a team with a specific role.
 */
@Getter
public class TeamMembership extends Entity<UUID> {
    private final UUID userId;
    private final UUID teamId;
    private final Role role;

    private TeamMembership(UUID userId, UUID teamId, Role role) {
        super(UUID.randomUUID());
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.teamId = Objects.requireNonNull(teamId, "Team ID cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
    }

    private TeamMembership(UUID id, UUID userId, UUID teamId, Role role,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.teamId = Objects.requireNonNull(teamId, "Team ID cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
    }

    /**
     * Creates a new team membership.
     *
     * @param userId the ID of the user
     * @param teamId the ID of the team
     * @param role   the role of the user in the team
     * @return a new TeamMembership instance
     */
    public static TeamMembership create(UUID userId, UUID teamId, Role role) {
        return new TeamMembership(userId, teamId, role);
    }

    /**
     * Creates a team membership for repository mapping.
     *
     * @param id         the membership ID
     * @param userId     the user ID
     * @param teamId     the team ID
     * @param role       the role
     * @param createdAt  the creation timestamp
     * @param updatedAt  the last update timestamp
     * @return a TeamMembership instance
     */
    public static TeamMembership fromRepository(UUID id, UUID userId, UUID teamId, Role role,
                                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new TeamMembership(id, userId, teamId, role, createdAt, updatedAt);
    }

    /**
     * Creates a new TeamMembership with the specified role.
     *
     * @param newRole the new role
     * @return a new TeamMembership instance with the updated role
     */
    public TeamMembership withRole(Role newRole) {
        return new TeamMembership(id, userId, teamId,
                Objects.requireNonNull(newRole, "New role cannot be null"),
                createdAt, LocalDateTime.now());
    }
}