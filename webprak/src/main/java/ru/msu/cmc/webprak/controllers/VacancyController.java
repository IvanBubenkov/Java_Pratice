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
public class VacancyController {

    private final VacancyDAO vacancyDAO;
    private final SelectedVacancyDAO selectedVacancyDAO;
    private final SiteUserDAO siteUserDAO;
    private final RoleDAO roleDAO;

    @Autowired
    public VacancyController(VacancyDAO vacancyDAO, SelectedVacancyDAO selectedVacancyDAO,
                             SiteUserDAO siteUserDAO, RoleDAO roleDAO) {
        this.vacancyDAO = vacancyDAO;
        this.selectedVacancyDAO = selectedVacancyDAO;
        this.siteUserDAO = siteUserDAO;
        this.roleDAO = roleDAO;
    }

    @GetMapping("/vacancies/{id}")
    public String getVacancy(@PathVariable Long id, Model model, Authentication authentication) {
        Vacancy vacancy = vacancyDAO.getById(id);
        if (vacancy == null) {
            return "redirect:/";
        }

        // Увеличиваем счетчик просмотров
        vacancy.setNumberOfViews(vacancy.getNumberOfViews() + 1);
        vacancyDAO.update(vacancy);

        // Проверяем, добавлена ли вакансия в избранное текущим пользователем
        boolean isFavorite = false;
        boolean isUser = false;

        if (authentication != null) {
            SiteUser currentUser = siteUserDAO.getUserByLogin(authentication.getName());
            if (currentUser != null) {
                Role userRole = roleDAO.getRoleByName("USER"); // Изменено с APPLICANT на USER

                if (userRole != null && currentUser.getRole() != null
                        && currentUser.getRole().getId().equals(userRole.getId())) {

                    isUser = true;

                    // Получаем все избранные вакансии пользователя
                    List<SelectedVacancy> favorites = selectedVacancyDAO.findByApplicantId(currentUser.getId());

                    // Проверяем, есть ли текущая вакансия в избранном
                    isFavorite = favorites.stream()
                            .anyMatch(fav -> fav.getVacancy() != null
                                    && fav.getVacancy().getId().equals(vacancy.getId()));
                }
            }
        }

        model.addAttribute("vacancy", vacancy);
        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("isUser", isUser); // Изменено с isApplicant на isUser
        return "vacancy";
    }

    @PostMapping("/vacancies/{id}/toggle-favorite")
    public String toggleFavorite(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        Vacancy vacancy = vacancyDAO.getById(id);
        if (vacancy == null) {
            return "redirect:/";
        }

        SiteUser user = siteUserDAO.getUserByLogin(authentication.getName());
        if (user == null) {
            return "redirect:/vacancies/" + id;
        }

        Role userRole = roleDAO.getRoleByName("USER"); // Изменено с APPLICANT на USER
        if (userRole == null || user.getRole() == null
                || !user.getRole().getId().equals(userRole.getId())) {
            return "redirect:/vacancies/" + id;
        }

        // Получаем все избранные вакансии пользователя
        List<SelectedVacancy> favorites = selectedVacancyDAO.findByApplicantId(user.getId());

        // Ищем текущую вакансию в избранном
        SelectedVacancy existing = favorites.stream()
                .filter(fav -> fav.getVacancy() != null
                        && fav.getVacancy().getId().equals(vacancy.getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            selectedVacancyDAO.delete(existing);
        } else {
            SelectedVacancy newFavorite = new SelectedVacancy(vacancy, user);
            selectedVacancyDAO.save(newFavorite);
        }

        return "redirect:/vacancies/" + id;
    }
}