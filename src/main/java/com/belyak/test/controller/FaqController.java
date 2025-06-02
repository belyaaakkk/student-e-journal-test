package com.belyak.test.controller;

import com.belyak.test.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping("/faq")
    public String showFaqPage(Model model) {
        model.addAttribute("faqs", faqService.getAllFaqs());
        return "faq";
    }
}