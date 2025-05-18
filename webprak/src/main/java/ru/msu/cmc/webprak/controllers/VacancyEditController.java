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
import java.util.List;

@Controller
@RequestMapping("/vacancies/edit")
public class VacancyEditController {

    @Autowired
    private VacancyDAO vacancyDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;

    @GetMapping("/{id}")
    public String editVacancyPage(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SiteUser currentUser = getAuthenticatedCompanyUser(auth);
        if (currentUser == null) {
            return "redirect:/auth";
        }

        Vacancy vacancy = vacancyDAO.getById(id);
        if (vacancy == null || !vacancy.getCompany().getId().equals(currentUser.getId())) {
            return "redirect:/company-vacancies";
        }

        prepareEditModel(model, vacancy);
        return "editVacancy";
    }

    @PostMapping("/save/{id}")
    public String saveVacancy(
            @PathVariable Long id,
            @RequestParam String vacancyName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) Long educationId,
            @RequestParam(required = false) String experienceRequirements) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SiteUser currentUser = getAuthenticatedCompanyUser(auth);
        if (currentUser == null) {
            return "redirect:/auth";
        }

        Vacancy vacancy = vacancyDAO.getById(id);
        if (vacancy == null || !vacancy.getCompany().getId().equals(currentUser.getId())) {
            return "redirect:/company-vacancies";
        }

        updateVacancy(vacancy, vacancyName, description, minSalary, educationId, experienceRequirements);
        vacancyDAO.update(vacancy);

        return "redirect:/company-vacancies";
    }

    @PostMapping("/delete/{id}")
    public String deleteVacancy(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SiteUser currentUser = getAuthenticatedCompanyUser(auth);
        if (currentUser == null) {
            return "redirect:/auth";
        }

        Vacancy vacancy = vacancyDAO.getById(id);
        if (vacancy != null && vacancy.getCompany().getId().equals(currentUser.getId())) {
            vacancyDAO.delete(vacancy);
        }

        return "redirect:/company-vacancies";
    }

    private SiteUser getAuthenticatedCompanyUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        SiteUser user = siteUserDAO.getUserByLogin(auth.getName());
        return (user != null && user.getRole().getRoleName().equals("COMPANY")) ? user : null;
    }

    private void prepareEditModel(Model model, Vacancy vacancy) {
        List<EducationalInstitution> allEducationLevels = (List<EducationalInstitution>) educationalInstitutionDAO.getAll();
        model.addAttribute("vacancy", vacancy);
        model.addAttribute("allEducationLevels", allEducationLevels);
    }

    private void updateVacancy(Vacancy vacancy, String name, String description,
                               BigDecimal salary, Long educationId, String experience) {
        vacancy.setVacancyName(name);
        vacancy.setDescription(description);
        vacancy.setMinSalary(salary);

        if (educationId != null) {
            EducationalInstitution education = educationalInstitutionDAO.getById(educationId);
            vacancy.setEducation(education);
        } else {
            vacancy.setEducation(null);
        }

        if (experience != null && !experience.isEmpty()) {
            vacancy.setDescription(
                    (vacancy.getDescription() != null ? vacancy.getDescription() + "\n\n" : "") +
                            "Требования к опыту:\n" + experience
            );
        }
    }
}