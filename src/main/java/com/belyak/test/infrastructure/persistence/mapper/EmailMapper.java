package com.belyak.test.infrastructure.persistence.mapper;

import com.belyak.test.domain.user.value.Email;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailMapper {
    default Email toEmail(String value) {
        return new Email(value);
    }

    default String fromEmail(Email email) {
        return email.value();
    }
}
