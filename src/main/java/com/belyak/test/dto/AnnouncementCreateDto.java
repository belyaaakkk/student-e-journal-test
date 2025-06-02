package com.belyak.test.dto;

import com.belyak.test.model.enums.AnnouncementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementCreateDto {
    private String title;
    private String content;
    private AnnouncementType type;
}
