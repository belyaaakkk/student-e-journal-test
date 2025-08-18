package com.belyak.test.infrastructure.persistence.mapper;

import com.belyak.test.domain.user.value.Username;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsernameMapper {
    default Username toUsername(String value) {
        return new Username(value);
    }

    default String fromUsername(Username username) {
        return username.value();
    }
}
