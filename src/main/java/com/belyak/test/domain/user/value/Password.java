package com.belyak.test.domain.user.value;

import com.belyak.test.ValidationUtils;

public record Password(String value) {
    public Password {
        value = ValidationUtils.validatePassword(value);
    }
}
