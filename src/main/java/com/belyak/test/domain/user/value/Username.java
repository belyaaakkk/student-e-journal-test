package com.belyak.test.domain.user.value;

import com.belyak.test.domain.application.auth.ValidationUtils;

public record Username(String value) {
    public Username {
        value = ValidationUtils.validateUsername(value);
    }
}
