package com.belyak.test.controller;

import com.belyak.test.dto.EditUserDto;
import com.belyak.test.dto.ReadUserDto;
import com.belyak.test.service.CountryService;
import com.belyak.test.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final CountryService countryService;

    @GetMapping
    public String profile(Model model,
                          @AuthenticationPrincipal UserDetails userDetails) {
        ReadUserDto userDto = userService.getByUsername(userDetails.getUsername());
        model.addAttribute("user", userDto);
        return "profile";
    }

    @GetMapping("/edit")
    public String showEditProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        ReadUserDto user = userService.getByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("countries", countryService.findAll());
        return "edit-profile";
    }

    @PostMapping("/edit")
    public String editUser(@Valid @ModelAttribute EditUserDto userDto,
                           BindingResult result,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            model.addAttribute("errors", result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList()));
            model.addAttribute("user", userService.getByUsername(userDetails.getUsername()));
            model.addAttribute("countries", countryService.findAll());
            return "edit-profile";
        }
        userService.updateUser(userDetails.getUsername(), userDto);
        return "redirect:/profile";
    }
}
