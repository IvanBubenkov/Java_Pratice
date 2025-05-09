package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;

@Controller
public class ProfileController {

    @Autowired
    private SiteUserDAO siteUserDAO;

    @GetMapping("/profile")
    public String profilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String login = authentication.getName();
        SiteUser user = siteUserDAO.getUserByLogin(login);

        if (user == null) {
            return "redirect:/login";
        }

        boolean isEmployer = "COMPANY".equals(user.getStatus().getStatusName());
        boolean isSeekingJob = "Ищет работу".equals(user.getStatus().getStatusName());

        model.addAttribute("user", user);
        model.addAttribute("isEmployer", isEmployer);
        model.addAttribute("isSeekingJob", isSeekingJob);

        return "profile";
    }
}