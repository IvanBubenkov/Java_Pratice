package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.*;
import ru.msu.cmc.webprak.models.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/company-vacancies")
public class CompanyVacanciesController {

    @Autowired
    private VacancyDAO vacancyDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;

    @GetMapping
    public String companyVacanciesPage(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Long educationId,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth";
        }

        SiteUser currentUser = siteUserDAO.getUserByLogin(auth.getName());
        if (currentUser == null || !currentUser.getRole().getRoleName().equals("COMPANY")) {
            return "redirect:/auth";
        }

        // Получаем все вакансии компании
        List<Vacancy> allVacancies = vacancyDAO.findByCriteria(
                null,
                currentUser.getId(),
                null,
                null
        );

        // Фильтрация вакансий
        List<Vacancy> filteredVacancies = allVacancies.stream()
                .filter(vacancy -> {
                    // Фильтр по должности
                    if (position != null && !position.isEmpty()) {
                        if (vacancy.getVacancyName() == null ||
                                !vacancy.getVacancyName().toLowerCase().contains(position.toLowerCase())) {
                            return false;
                        }
                    }

                    // Фильтр по образованию
                    if (educationId != null) {
                        if (vacancy.getEducation() == null ||
                                !vacancy.getEducation().getId().equals(educationId)) {
                            return false;
                        }
                    }

                    // Фильтр по зарплате
                    if (minSalary != null && vacancy.getMinSalary() != null) {
                        if (vacancy.getMinSalary().compareTo(minSalary) < 0) {
                            return false;
                        }
                    }
                    if (maxSalary != null && vacancy.getMinSalary() != null) {
                        if (vacancy.getMinSalary().compareTo(maxSalary) > 0) {
                            return false;
                        }
                    }

                    return true;
                })
                .sorted((v1, v2) -> Long.compare(v2.getNumberOfViews(), v1.getNumberOfViews()))
                .collect(Collectors.toList());

        // Данные для фильтров
        Set<String> availablePositions = allVacancies.stream()
                .map(Vacancy::getVacancyName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        model.addAttribute("vacancies", filteredVacancies);
        model.addAttribute("allEducationLevels", educationalInstitutionDAO.getAll());
        model.addAttribute("availablePositions", availablePositions);
        model.addAttribute("filterPosition", position);
        model.addAttribute("filterEducationId", educationId);
        model.addAttribute("filterMinSalary", minSalary);
        model.addAttribute("filterMaxSalary", maxSalary);

        return "companyVacancies";
    }

    @PostMapping("/delete/{id}")
    public String deleteVacancy(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth";
        }

        SiteUser currentUser = siteUserDAO.getUserByLogin(auth.getName());
        if (currentUser == null || !currentUser.getRole().getRoleName().equals("COMPANY")) {
            return "redirect:/auth";
        }

        Vacancy vacancy = vacancyDAO.getById(id);
        if (vacancy != null && vacancy.getCompany().getId().equals(currentUser.getId())) {
            vacancyDAO.delete(vacancy);
        }

        return "redirect:/company-vacancies";
    }

    @GetMapping("/create")
    public String createNewVacancy() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth";
        }

        SiteUser currentUser = siteUserDAO.getUserByLogin(auth.getName());
        if (currentUser == null || !currentUser.getRole().getRoleName().equals("COMPANY")) {
            return "redirect:/auth";
        }

        // Создаем новую вакансию с минимальными данными
        Vacancy newVacancy = new Vacancy();
        newVacancy.setCompany(currentUser);
        newVacancy.setVacancyName("Новая вакансия");
        newVacancy.setNumberOfViews(0L);
        vacancyDAO.save(newVacancy);

        // Перенаправляем на страницу редактирования новой вакансии
        return "redirect:/vacancies/edit/" + newVacancy.getId();
    }
}