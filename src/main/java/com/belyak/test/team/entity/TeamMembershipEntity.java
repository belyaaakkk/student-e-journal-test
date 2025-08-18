package com.belyak.test.team.entity;

import com.belyak.test.user.domain.Role;
import com.belyak.test.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity representing a user's membership in a team.
 */
@Getter
@Entity
@Table(name = "team_memberships", indexes = {
        @Index(name = "idx_team_memberships_user_id", columnList = "user_id"),
        @Index(name = "idx_team_memberships_role", columnList = "role")
})
@IdClass(TeamMembershipId.class)
public class TeamMembershipEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    protected TeamMembershipEntity() {
    }

    /**
     * Creates a new team membership entity.
     *
     * @param team the team
     * @param user the user
     * @param role the role of the user in the team
     */
    public TeamMembershipEntity(TeamEntity team, UserEntity user, Role role) {
        this.team = Objects.requireNonNull(team, "Team cannot be null");
        this.user = Objects.requireNonNull(user, "User cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.joinedAt = LocalDateTime.now();
    }

    /**
     * Updates the role of the member.
     *
     * @param newRole the new role
     */
    public void setRole(Role newRole) {
        this.role = Objects.requireNonNull(newRole, "New role cannot be null");
    }
}