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
@RequestMapping("/favorites")
public class FavoritesController {

    @Autowired
    private SelectedVacancyDAO selectedVacancyDAO;

    @Autowired
    private SelectedResumeDAO selectedResumeDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private VacancyDAO vacancyDAO;

    @Autowired
    private ResumeDAO resumeDAO;

    @GetMapping
    public String favoritesPage(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) BigDecimal minSalary,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth";
        }

        SiteUser currentUser = siteUserDAO.getUserByLogin(auth.getName());
        if (currentUser == null) {
            return "redirect:/auth";
        }

        boolean isEmployer = currentUser.getRole().getRoleName().equals("COMPANY");
        model.addAttribute("isEmployer", isEmployer);

        // Получаем список всех возможных должностей и компаний для фильтров
        if (isEmployer) {
            List<String> positions = resumeDAO.getAll().stream()
                    .map(Resume::getResumeName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            model.addAttribute("positions", positions);
        } else {
            List<String> positions = vacancyDAO.getAll().stream()
                    .map(Vacancy::getVacancyName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            List<String> companies = vacancyDAO.getAll().stream()
                    .map(v -> v.getCompany() != null ? v.getCompany().getFullNameCompany() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            model.addAttribute("positions", positions);
            model.addAttribute("companies", companies);
        }

        if (isEmployer) {
            // Для работодателя - избранные резюме
            List<SelectedResume> selectedResumes = selectedResumeDAO.findByCompanyId(currentUser.getId());
            List<Resume> resumes = selectedResumes.stream()
                    .map(SelectedResume::getResume)
                    .filter(Objects::nonNull)
                    .filter(resume -> filterResume(resume, position, minSalary))
                    .collect(Collectors.toList());

            model.addAttribute("resumes", resumes);
        } else {
            // Для соискателя - избранные вакансии
            List<SelectedVacancy> selectedVacancies = selectedVacancyDAO.findByApplicantId(currentUser.getId());
            List<Vacancy> vacancies = selectedVacancies.stream()
                    .map(SelectedVacancy::getVacancy)
                    .filter(Objects::nonNull)
                    .filter(vacancy -> filterVacancy(vacancy, position, company, minSalary))
                    .collect(Collectors.toList());

            model.addAttribute("vacancies", vacancies);
        }

        model.addAttribute("filterPosition", position);
        model.addAttribute("filterCompany", company);
        model.addAttribute("filterMinSalary", minSalary);

        return "favorites";
    }

    private boolean filterResume(Resume resume, String position, BigDecimal minSalary) {
        if (position != null && !position.isEmpty() &&
                (resume.getResumeName() == null ||
                        !resume.getResumeName().toLowerCase().contains(position.toLowerCase()))) {
            return false;
        }
        if (minSalary != null && resume.getMinSalaryRequirement() != null &&
                resume.getMinSalaryRequirement().compareTo(minSalary) < 0) {
            return false;
        }
        return true;
    }

    private boolean filterVacancy(Vacancy vacancy, String position, String company, BigDecimal minSalary) {
        if (position != null && !position.isEmpty() &&
                (vacancy.getVacancyName() == null ||
                        !vacancy.getVacancyName().toLowerCase().contains(position.toLowerCase()))) {
            return false;
        }
        if (company != null && !company.isEmpty() &&
                (vacancy.getCompany() == null ||
                        !vacancy.getCompany().getFullNameCompany().toLowerCase().contains(company.toLowerCase()))) {
            return false;
        }
        if (minSalary != null && vacancy.getMinSalary() != null &&
                vacancy.getMinSalary().compareTo(minSalary) < 0) {
            return false;
        }
        return true;
    }

    @PostMapping("/remove-vacancy/{id}")
    public String removeFavoriteVacancy(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SiteUser currentUser = siteUserDAO.getUserByLogin(auth.getName());
        if (currentUser == null || currentUser.getRole().getRoleName().equals("COMPANY")) {
            return "redirect:/auth";
        }

        SelectedVacancy favorite = selectedVacancyDAO.getById(id);
        if (favorite != null && favorite.getApplicant().getId().equals(currentUser.getId())) {
            selectedVacancyDAO.delete(favorite);
        }

        return "redirect:/favorites";
    }

    @PostMapping("/remove-resume/{id}")
    public String removeFavoriteResume(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SiteUser currentUser = siteUserDAO.getUserByLogin(auth.getName());
        if (currentUser == null || !currentUser.getRole().getRoleName().equals("COMPANY")) {
            return "redirect:/auth";
        }

        SelectedResume favorite = selectedResumeDAO.getById(id);
        if (favorite != null && favorite.getCompany().getId().equals(currentUser.getId())) {
            selectedResumeDAO.delete(favorite);
        }

        return "redirect:/favorites";
    }
}