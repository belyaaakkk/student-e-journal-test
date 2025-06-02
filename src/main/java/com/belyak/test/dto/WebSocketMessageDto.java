package com.belyak.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDto {
    private String senderName;
    private String senderUsername;
    private String content;
    private String timestamp;
}
