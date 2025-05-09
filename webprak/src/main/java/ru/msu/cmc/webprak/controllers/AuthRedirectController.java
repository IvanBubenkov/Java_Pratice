package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.models.SiteUser;

@Controller
public class AuthRedirectController {

    @Autowired
    private SiteUserDAO siteUserDAO;

    @GetMapping("/determine-role-redirect")
    public String determineRedirect(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth";
        }

        SiteUser user = siteUserDAO.getUserByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/auth";
        }

        return "ADMIN".equals(user.getRole().getRoleName())
                ? "redirect:/admin/panel"
                : "redirect:/profile";
    }
}