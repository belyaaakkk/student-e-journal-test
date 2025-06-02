package com.belyak.test.mapper;

import com.belyak.test.dto.ReadTeacherDto;
import com.belyak.test.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(source = "user.firstname", target = "firstname")
    @Mapping(source = "user.lastname", target = "lastname")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "user.bio", target = "bio")
    @Mapping(source = "user.photoUrl", target = "photoUrl")
    @Mapping(source = "subject.code", target = "subjectCode", defaultValue = "UNKNOWN")
    ReadTeacherDto toReadTeacherDto(Teacher teacher);
}
