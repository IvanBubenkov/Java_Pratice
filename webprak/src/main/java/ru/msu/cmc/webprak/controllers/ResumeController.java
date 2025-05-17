package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.*;
import ru.msu.cmc.webprak.models.*;

import java.util.List;

@Controller
public class ResumeController {

    private final ResumeDAO resumeDAO;
    private final SelectedResumeDAO selectedResumeDAO;
    private final SiteUserDAO siteUserDAO;
    private final Work_historyDAO workHistoryDAO;
    private final RoleDAO roleDAO;

    @Autowired
    public ResumeController(ResumeDAO resumeDAO, SelectedResumeDAO selectedResumeDAO,
                            SiteUserDAO siteUserDAO, Work_historyDAO workHistoryDAO, RoleDAO roleDAO) {
        this.resumeDAO = resumeDAO;
        this.selectedResumeDAO = selectedResumeDAO;
        this.siteUserDAO = siteUserDAO;
        this.workHistoryDAO = workHistoryDAO;
        this.roleDAO = roleDAO;
    }

    @GetMapping("/resumes/{id}")
    public String getResume(@PathVariable Long id, Model model, Authentication authentication) {
        Resume resume = resumeDAO.getById(id);
        if (resume == null) {
            return "redirect:/";
        }

        // Увеличиваем счетчик просмотров
        resume.setNumberOfViews(resume.getNumberOfViews() + 1);
        resumeDAO.save(resume);

        // Получаем историю работы для пользователя
        List<WorkHistory> workHistory = workHistoryDAO.findByCriteria(
                null,       // vacancyName - не фильтруем
                resume.getUser(),  // applicant - текущий пользователь резюме
                null,       // company - не фильтруем
                null,       // minSalary - не фильтруем
                null,       // startDate - не фильтруем
                null        // endDate - не фильтруем
        );

        // Проверяем, добавлено ли резюме в избранное текущей компанией
        boolean isFavorite = false;
        boolean isCompany = false;

        if (authentication != null) {
            SiteUser currentUser = siteUserDAO.getUserByLogin(authentication.getName());
            Role companyRole = roleDAO.getRoleByName("COMPANY");

            if (currentUser.getRole().getId().equals(companyRole.getId())) {
                isCompany = true;
                isFavorite = selectedResumeDAO.existsByResumeAndCompany(resume, currentUser);
            }
        }

        model.addAttribute("resume", resume);
        model.addAttribute("workHistory", workHistory);
        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("isCompany", isCompany);
        return "resume";
    }

    @PostMapping("/resumes/{id}/toggle-favorite")
    public String toggleFavorite(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        Resume resume = resumeDAO.getById(id);
        if (resume == null) {
            return "redirect:/";
        }

        SiteUser company = siteUserDAO.getUserByLogin(authentication.getName());
        Role companyRole = roleDAO.getRoleByName("COMPANY");

        if (!company.getRole().getId().equals(companyRole.getId())) {
            return "redirect:/resumes/" + id;
        }

        SelectedResume existing = selectedResumeDAO.findByResumeAndCompany(resume, company);
        if (existing != null) {
            selectedResumeDAO.delete(existing);
        } else {
            SelectedResume newFavorite = new SelectedResume(resume, company);
            selectedResumeDAO.save(newFavorite);
        }

        return "redirect:/resumes/" + id;
    }
}