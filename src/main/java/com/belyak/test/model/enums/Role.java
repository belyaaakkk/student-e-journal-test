package com.belyak.test.model.enums;

import org.springframework.security.core.GrantedAuthority;

// === ENUMS ===
public enum Role implements GrantedAuthority {
    STUDENT,
    PARENT,
    TEACHER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
