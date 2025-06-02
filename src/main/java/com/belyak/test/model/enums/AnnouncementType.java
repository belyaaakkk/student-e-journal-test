package com.belyak.test.model.enums;

import lombok.Getter;

@Getter
public enum AnnouncementType {
    GENERAL("type.general"),
    URGENT("type.urgent"),
    FOR_PARENTS("type.for_parents"),
    FOR_STUDENTS("type.for_students");

    private final String displayName;

    AnnouncementType(String displayName) {
        this.displayName = displayName;
    }
}
