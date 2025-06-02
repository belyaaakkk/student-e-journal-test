package com.belyak.test.mapper;

import com.belyak.test.dto.EventReadDto;
import com.belyak.test.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "className", source = "schoolClass.code")
    @Mapping(target = "subjectCode", expression = "java(event.getSchedule() != null ? event.getSchedule().getSubject().getCode() : event.getHomework().getSubject().getCode())")
    @Mapping(target = "teacherName", expression = "java(event.getSchedule() != null ? event.getSchedule().getTeacher().getUser().getFirstname() + ' ' + event.getSchedule().getTeacher().getUser().getLastname() : event.getHomework().getTeacher().getUser().getFirstname() + ' ' + event.getHomework().getTeacher().getUser().getLastname())")
    @Mapping(target = "room", source = "schedule.room")
    @Mapping(target = "homeworkId", source = "homework.id")
    @Mapping(target = "homeworkStatus", source = "homework.status")
    @Mapping(target = "eventType", source = "eventType")
    EventReadDto toDto(Event event);
}