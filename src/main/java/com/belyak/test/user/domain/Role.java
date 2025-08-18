package com.belyak.test.user.domain;

/**
 * Enum representing user roles with a permission hierarchy.
 */
public enum Role {
    USER, MANAGER, ADMIN;

    /**
     * Checks if this role has at least the permissions of the required role.
     *
     * @param requiredRole the required role
     * @return true if this role has sufficient permissions, false otherwise
     */
    public boolean hasPermission(Role requiredRole) {
        return switch (requiredRole) {
            case USER -> this == USER || this == MANAGER || this == ADMIN;
            case MANAGER -> this == MANAGER || this == ADMIN;
            case ADMIN -> this == ADMIN;
        };
    }
}