package com.belyak.test.mapper;

import com.belyak.test.dto.ReadGradeDto;
import com.belyak.test.model.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        StudentMapper.class,
        TeacherMapper.class
})
public interface GradeMapper {

    @Mapping(source = "student", target = "student")
    @Mapping(source = "teacher", target = "teacher")
    @Mapping(source = "subject.messageKey", target = "subjectMessageKey", defaultValue = "UNKNOWN")
    ReadGradeDto toReadGradeDto(Grade grade);
}
