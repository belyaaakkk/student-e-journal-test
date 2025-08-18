package com.belyak.test.team.entity;

import com.belyak.test.user.domain.Role;
import com.belyak.test.user.entity.BasicEntity;
import com.belyak.test.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * JPA entity representing a team.
 */
@Entity
@Table(name = "teams", indexes = {
        @Index(name = "idx_teams_created_by", columnList = "created_by"),
        @Index(name = "idx_teams_active", columnList = "active"),
        @Index(name = "idx_teams_access_code", columnList = "access_code")
})
public class TeamEntity extends BasicEntity {

    @Getter
    @Column(name = "name", nullable = false, length = 100)
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @Setter
    @Getter
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Getter
    @AttributeOverride(name = "value", column = @Column(name = "access_code"))
    private AccessCodeEntity accessCodeEntity;

    @Setter
    @Getter
    @AttributeOverride(name = "value", column = @Column(name = "access_password"))
    private AccessPasswordEntity accessPasswordEntity;

    @Getter
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private UserEntity createdBy;

    @Getter
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TeamMembershipEntity> memberships = new HashSet<>();

    protected TeamEntity() {
    }

    /**
     * Creates a new team entity.
     *
     * @param name              the team name
     * @param description       the team description (nullable)
     * @param accessCodeEntity  the access code
     * @param accessPasswordEntity the access password
     * @param createdBy         the user who created the team
     */
    public TeamEntity(String name, String description, AccessCodeEntity accessCodeEntity,
                      AccessPasswordEntity accessPasswordEntity, UserEntity createdBy) {
        this.name = Objects.requireNonNull(name, "Name cannot be null").trim();
        this.description = description != null ? description : "";
        this.accessCodeEntity = Objects.requireNonNull(accessCodeEntity, "AccessCodeEntity cannot be null");
        this.accessPasswordEntity = Objects.requireNonNull(accessPasswordEntity, "AccessPasswordEntity cannot be null");
        this.createdBy = Objects.requireNonNull(createdBy, "CreatedBy cannot be null");
        addMember(createdBy, Role.ADMIN);
    }

    /**
     * Adds a member to the team with the specified role.
     *
     * @param user the user to add
     * @param role the role of the user
     */
    public void addMember(UserEntity user, Role role) {
        TeamMembershipEntity membership = new TeamMembershipEntity(this, user, role);
        this.memberships.add(membership);
        user.getTeamMembershipEntities().add(membership);
    }

    /**
     * Removes a member from the team.
     *
     * @param user the user to remove
     */
    public void removeMember(UserEntity user) {
        memberships.removeIf(membership -> membership.getUser().equals(user));
    }

    /**
     * Checks if a user is a member with at least the specified role.
     *
     * @param user        the user to check
     * @param minimumRole the minimum role required
     * @return true if the user has the required role, false otherwise
     */
    public boolean hasMember(UserEntity user, Role minimumRole) {
        return memberships.stream()
                .anyMatch(membership ->
                        membership.getUser().equals(user) &&
                        membership.getRole().hasPermission(minimumRole)
                );
    }

    public void deactivate() {
        this.active = false;
    }

    public Set<TeamMembershipEntity> getMemberships() {
        return Collections.unmodifiableSet(memberships);
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null").trim();
    }
}