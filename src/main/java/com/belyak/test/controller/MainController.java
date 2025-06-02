package com.belyak.test.controller;

import com.belyak.test.dto.ReadUserDto;
import com.belyak.test.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;

    @GetMapping({"/", "/home"})
    public String home(Model model,
                       @AuthenticationPrincipal UserDetails userDetails) {
        ReadUserDto userDto = userService.getByUsername(userDetails.getUsername());
        model.addAttribute("user", userDto);
        return "home";
    }

    @GetMapping("/login")
    public String profile() {
        return "login";
    }

    @GetMapping("/terms-of-use")
    public String termsOfUse(Model model) {
        model.addAttribute("pageTitle", "#{title.terms_of_use}");
        return "terms-of-use";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy(Model model) {
        model.addAttribute("pageTitle", "#{title.privacy_policy}");
        return "privacy-policy";
    }

    @GetMapping("/support")
    public String support() {
        return "support";
    }

    @PostMapping("/support")
    public String send_request() {
        return "support";
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }
}
