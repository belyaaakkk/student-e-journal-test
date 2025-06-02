package com.belyak.test.model.enums;

import lombok.Getter;

@Getter
public enum HomeworkStatus {
    ASSIGNED("status.assigned"),
    OVERDUE("status.overdue"),
    COMPLETED("status.completed");

    private final String displayName;

    HomeworkStatus(String displayName) {
        this.displayName = displayName;
    }
}
