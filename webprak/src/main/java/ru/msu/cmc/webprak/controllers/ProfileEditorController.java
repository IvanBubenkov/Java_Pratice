package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.*;
import ru.msu.cmc.webprak.models.*;

@Controller
@RequestMapping("/profile/edit")
public class ProfileEditorController {
    private final SiteUserDAO siteUserDAO;
    private final UserStatusDAO userStatusDAO;

    @Autowired
    public ProfileEditorController(SiteUserDAO siteUserDAO,
                                   UserStatusDAO userStatusDAO) {
        this.siteUserDAO = siteUserDAO;
        this.userStatusDAO = userStatusDAO;
    }

    @GetMapping("")
    public String editProfileForm(Model model, Authentication authentication) {
        String login = authentication.getName();
        SiteUser user = siteUserDAO.getUserByLogin(login);

        model.addAttribute("user", user);
        model.addAttribute("statuses", userStatusDAO.getAll());

        return "profileEditor";
    }

    @PostMapping("")
    public String saveProfile(
            @RequestParam String fullNameCompany,
            @RequestParam String email,
            @RequestParam String homeAddress,
            @RequestParam Long statusId,
            Authentication authentication) {

        String login = authentication.getName();
        SiteUser user = siteUserDAO.getUserByLogin(login);
        UserStatus status = userStatusDAO.getById(statusId);

        user.setFullNameCompany(fullNameCompany);
        user.setEmail(email);
        user.setHomeAddress(homeAddress);
        user.setStatus(status);

        siteUserDAO.save(user);

        return "redirect:/profile";
    }
}