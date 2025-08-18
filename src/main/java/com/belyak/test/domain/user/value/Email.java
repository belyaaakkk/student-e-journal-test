package com.belyak.test.domain.user.value;

import com.belyak.test.ValidationUtils;

public record Email(String value) {
    public Email {
        value = ValidationUtils.validateEmail(value);
    }
}
