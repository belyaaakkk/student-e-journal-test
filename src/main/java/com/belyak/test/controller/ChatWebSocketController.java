package com.belyak.test.controller;

import com.belyak.test.dto.ChatMessageDto;
import com.belyak.test.dto.WebSocketMessageDto;
import com.belyak.test.model.Message;
import com.belyak.test.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto chatMessage, Principal principal) {
        Message savedMessage = messageService.saveMessageFromWebSocket(chatMessage.getContent(), principal.getName());

        WebSocketMessageDto messageDto = new WebSocketMessageDto(
                savedMessage.getSender().getFirstname() + " " + savedMessage.getSender().getLastname(),
                savedMessage.getSender().getUsername(),
                savedMessage.getContent(),
                savedMessage.getTimestamp().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
        );


        messagingTemplate.convertAndSend(
                "/topic/class/" + savedMessage.getClassChat().getSchoolClass().getCode(),
                messageDto
        );
    }
}