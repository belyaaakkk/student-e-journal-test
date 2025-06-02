package com.belyak.test.mapper;

import com.belyak.test.dto.HomeworkReadDto;
import com.belyak.test.model.Homework;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HomeworkMapper {

    @Mapping(source = "subject.code", target = "subjectName")
    @Mapping(source = "schoolClass.code", target = "className")
    @Mapping(expression = "java(homework.getTeacher().getUser().getFirstname() + \" \" + homework.getTeacher().getUser().getLastname())", target = "teacherFullName")
    HomeworkReadDto toHomeworkReadDto(Homework homework);

    List<HomeworkReadDto> toHomeworkReadDtoList(List<Homework> homeworkList);
}

