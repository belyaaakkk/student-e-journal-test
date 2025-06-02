package com.belyak.test.controller;

import com.belyak.test.dto.AnnouncementCreateDto;
import com.belyak.test.model.Announcement;
import com.belyak.test.model.enums.AnnouncementType;
import com.belyak.test.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public String showAnnouncements(@RequestParam(name = "typeFilter", required = false) AnnouncementType type,
                                    Model model) {
        List<Announcement> announcements;

        announcements = announcementService.getAllAnnouncements(type);

        model.addAttribute("announcements", announcements);
        model.addAttribute("selectedType", type);
        model.addAttribute("types", AnnouncementType.values());

        return "announcements";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public String showAddAnnouncementForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        model.addAttribute("announcementTypes", AnnouncementType.values());
        return "add-announcement";
    }

    @PostMapping("/add")
    public String addAnnouncement(@ModelAttribute("announcement") AnnouncementCreateDto dto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        announcementService.createAnnouncement(dto, userDetails.getUsername());
        return "redirect:/announcements";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public String deleteAnnouncement(@PathVariable("id") Long id,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        announcementService.deleteAnnouncement(id, userDetails.getUsername());
        return "redirect:/announcements";
    }
}
