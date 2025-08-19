package com.belyak.test.domain.user.value;

import com.belyak.test.domain.application.auth.ValidationUtils;

public record Password(String value) {
    public Password {
        value = ValidationUtils.validatePassword(value);
    }
}
