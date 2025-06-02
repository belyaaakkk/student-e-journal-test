package com.belyak.test.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ParentReadDto {
    Long id;
    ReadUserDto user;
    List<ReadStudentDto> children;
}
