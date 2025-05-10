package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.SelectedResume;
import ru.msu.cmc.webprak.models.SelectedVacancy;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.DAO.SelectedResumeDAO;
import ru.msu.cmc.webprak.DAO.SelectedVacancyDAO;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private SelectedResumeDAO selectedResumeDAO;

    @Autowired
    private SelectedVacancyDAO selectedVacancyDAO;

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

        // Получение ролей пользователя из Authentication
        boolean isEmployer = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_COMPANY"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

        model.addAttribute("user", user);
        model.addAttribute("isEmployer", isEmployer);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isUser", isUser);

        // Дополнительная логика в зависимости от роли
        if (isEmployer) {
            List<SelectedResume> selectedResumes = selectedResumeDAO.findByCompanyId(user.getId());
            model.addAttribute("selectedResumes", selectedResumes);
        } else if (isUser) {
            List<SelectedVacancy> selectedVacancies = selectedVacancyDAO.findByApplicantId(user.getId());
            model.addAttribute("selectedVacancies", selectedVacancies);
        }

        return "profile";
    }


}
