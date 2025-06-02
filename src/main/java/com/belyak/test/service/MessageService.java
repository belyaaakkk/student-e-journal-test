package com.belyak.test.service;

import com.belyak.test.model.*;
import com.belyak.test.repository.ClassChatRepository;
import com.belyak.test.repository.MessageRepository;
import com.belyak.test.repository.StudentRepository;
import com.belyak.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final StudentRepository studentRepository;
    private final ClassChatRepository classChatRepository;
    private final UserRepository userRepository;

    public List<Message> getMessagesByClassCode(String classCode) {
        return messageRepository.findAllByClassChat_SchoolClass_CodeOrderByTimestampAsc(classCode);
    }

    public void sendMessageToClass(String content, String username) {
        Message message = buildMessage(content, username);
        messageRepository.save(message);
    }

    @Transactional
    public Message saveMessageFromWebSocket(String content, String username) {
        return messageRepository.save(buildMessage(content, username));
    }


    private Message buildMessage(String content, String username) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Student student = studentRepository.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        SchoolClass className = student.getSchoolClass();
        ClassChat chat = classChatRepository.findBySchoolClass_Code(className.getCode())
                .orElseGet(() -> classChatRepository.save(new ClassChat(null, className, new ArrayList<>())));

        return Message.builder()
                .content(content)
                .sender(sender)
                .timestamp(LocalDateTime.now())
                .classChat(chat)
                .build();
    }
}
