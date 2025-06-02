package com.belyak.test.dto;

import com.belyak.test.model.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventReadDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String className;
    private EventType eventType;
    private String subjectCode;
    private String teacherName;
    private String room;
    private Long homeworkId;
    private String homeworkStatus;
}