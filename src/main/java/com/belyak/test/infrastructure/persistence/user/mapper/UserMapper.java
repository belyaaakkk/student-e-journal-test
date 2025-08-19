package com.belyak.test.infrastructure.persistence.user.mapper;

import com.belyak.test.domain.user.model.User;
import com.belyak.test.infrastructure.persistence.user.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        UsernameMapper.class, EmailMapper.class, PasswordMapper.class})
public interface UserMapper {

    User toDomain(UserJpaEntity entity);

    UserJpaEntity toEntity(User domain);
}

