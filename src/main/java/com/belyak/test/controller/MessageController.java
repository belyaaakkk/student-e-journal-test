package com.belyak.test.controller;

import com.belyak.test.model.Message;
import com.belyak.test.model.Student;
import com.belyak.test.service.MessageService;
import com.belyak.test.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final StudentService studentService;

    @GetMapping("/messages")
    public String showChat(Principal principal, Model model) {
        Student student = studentService.findByUsername(principal.getName());

        List<Message> messages = messageService.getMessagesByClassCode(student.getSchoolClass().getCode());
        model.addAttribute("messages", messages);
        model.addAttribute("className", student.getSchoolClass().getCode());
        return "messages";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("content") String content, Principal principal) {
        messageService.sendMessageToClass(content, principal.getName());
        return "redirect:/messages";
    }
}
