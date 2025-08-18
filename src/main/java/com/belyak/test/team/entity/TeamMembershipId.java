package com.belyak.test.team.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Composite key for TeamMembershipEntity.
 */
public class TeamMembershipId implements Serializable {
    private UUID team;
    private UUID user;

    public TeamMembershipId() {
    }

    /**
     * Creates a composite key for a team membership.
     *
     * @param team the team ID
     * @param user the user ID
     */
    public TeamMembershipId(UUID team, UUID user) {
        this.team = team;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamMembershipId)) return false;
        TeamMembershipId that = (TeamMembershipId) o;
        return Objects.equals(team, that.team) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, user);
    }
}