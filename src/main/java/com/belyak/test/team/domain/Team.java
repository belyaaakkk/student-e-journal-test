package com.belyak.test.team.domain;

import com.belyak.test.user.domain.Entity;
import com.belyak.test.user.domain.Role;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a team with members, projects, and access controls.
 */
@Getter
public class Team extends Entity<UUID> {

    private String name;
    private String description;
    private final UUID createdBy;
    private boolean active;
    private final AccessCode accessCode;
    private final AccessPassword accessPassword;
    private final List<UUID> projectIds;
    private final Map<UUID, Role> memberships;

    /**
     * Creates a new team with the specified attributes.
     *
     * @param name           the team name
     * @param description    the team description (nullable)
     * @param createdBy      the ID of the user creating the team
     * @param accessCode     the access code for joining the team
     * @param accessPassword the access password for joining the team
     */
    private Team(String name, String description, UUID createdBy, AccessCode accessCode, AccessPassword accessPassword) {
        super(UUID.randomUUID());
        this.name = validateName(name);
        this.description = description != null ? description : "";
        this.createdBy = Objects.requireNonNull(createdBy, "CreatedBy cannot be null");
        this.active = true;
        this.accessCode = Objects.requireNonNull(accessCode, "AccessCode cannot be null");
        this.accessPassword = Objects.requireNonNull(accessPassword, "AccessPassword cannot be null");
        this.projectIds = new ArrayList<>();
        this.memberships = new HashMap<>();
        this.memberships.put(createdBy, Role.ADMIN);
    }

    /**
     * Creates a team instance for repository mapping.
     *
     * @param id             the team ID
     * @param name           the team name
     * @param description    the team description (nullable)
     * @param createdBy      the ID of the user who created the team
     * @param active         whether the team is active
     * @param accessCode     the access code
     * @param accessPassword the access password
     * @param projectIds     the list of project IDs
     * @param memberships    the map of member IDs to roles
     * @param createdAt      the creation timestamp
     * @param updatedAt      the last update timestamp
     */
    public Team(UUID id, String name, String description, UUID createdBy, boolean active, AccessCode accessCode,
                AccessPassword accessPassword, List<UUID> projectIds, Map<UUID, Role> memberships,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.name = validateName(name);
        this.description = description != null ? description : "";
        this.createdBy = Objects.requireNonNull(createdBy, "CreatedBy cannot be null");
        this.active = active;
        this.accessCode = Objects.requireNonNull(accessCode, "AccessCode cannot be null");
        this.accessPassword = Objects.requireNonNull(accessPassword, "AccessPassword cannot be null");
        this.projectIds = projectIds != null ? new ArrayList<>(projectIds) : new ArrayList<>();
        this.memberships = memberships != null ? new HashMap<>(memberships) : new HashMap<>();
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Team name cannot exceed 100 characters");
        }
        return name.trim();
    }

    /**
     * Returns an unmodifiable list of member IDs.
     *
     * @return list of member IDs
     */
    public List<UUID> getMemberIds() {
        return Collections.unmodifiableList(new ArrayList<>(memberships.keySet()));
    }

    /**
     * Returns an unmodifiable list of project IDs.
     *
     * @return list of project IDs
     */
    public List<UUID> getProjectIds() {
        return Collections.unmodifiableList(projectIds);
    }

    public void updateName(String newName) {
        this.name = validateName(newName);
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription != null ? newDescription : "";
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void addMember(UUID userId, Role role) {
        this.memberships.put(Objects.requireNonNull(userId, "User ID cannot be null"),
                Objects.requireNonNull(role, "Role cannot be null"));
    }

    public void removeMember(UUID userId) {
        this.memberships.remove(userId);
    }

    public void updateMemberRole(UUID userId, Role newRole) {
        if (memberships.containsKey(userId)) {
            memberships.put(userId, Objects.requireNonNull(newRole, "New role cannot be null"));
        }
    }

    public void addProject(UUID projectId) {
        this.projectIds.add(Objects.requireNonNull(projectId, "Project ID cannot be null"));
    }

    public void removeProject(UUID projectId) {
        this.projectIds.remove(projectId);
    }

    public boolean isMember(UUID userId) {
        return memberships.containsKey(userId);
    }

    public Role getMemberRole(UUID userId) {
        return memberships.get(userId);
    }
}