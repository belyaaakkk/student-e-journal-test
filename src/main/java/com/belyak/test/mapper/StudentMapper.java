package com.belyak.test.mapper;

import com.belyak.test.dto.ReadStudentDto;
import com.belyak.test.model.Student;
import com.belyak.test.model.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface StudentMapper {

    @Mapping(target = "firstName", source = "user.firstname")
    @Mapping(target = "lastName", source = "user.lastname")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phoneNumber", source = "user.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "user.dateOfBirth")
    @Mapping(target = "bio", source = "user.bio")
    @Mapping(target = "classCode", source = "schoolClass.code")
    @Mapping(target = "role", source = "user.role", qualifiedByName = "roleToString")
    ReadStudentDto toReadStudentDto(Student student);
}

