package com.belyak.test.mapper;

import com.belyak.test.dto.ReadUserDto;
import com.belyak.test.model.User;
import com.belyak.test.model.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "firstName", source = "firstname")
    @Mapping(target = "lastName", source = "lastname")
    @Mapping(target = "role", source = "role", qualifiedByName = "roleToString")
    @Mapping(target = "countryCode", source = "country.messageKey")
    ReadUserDto toReadUserDto(User user);

    @Named("roleToString")
    static String roleToString(Role role) {
        return role != null ? role.name().toLowerCase() : null;
    }
}