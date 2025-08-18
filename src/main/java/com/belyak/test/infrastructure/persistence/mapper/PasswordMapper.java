package com.belyak.test.infrastructure.persistence.mapper;

import com.belyak.test.domain.user.value.Password;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordMapper {
    default Password toPassword(String value) {
        return new Password(value);
    }

    default String fromPassword(Password password) {
        return password.value();
    }
}
