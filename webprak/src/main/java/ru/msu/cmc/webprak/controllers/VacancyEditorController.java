package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.*;
import ru.msu.cmc.webprak.models.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/employer/vacancy")
public class VacancyEditorController {
    private final VacancyDAO vacancyDAO;
    private final EducationalInstitutionDAO educationDAO;
    private final SiteUserDAO siteUserDAO;
    private final RoleDAO roleDAO;

    @Autowired
    public VacancyEditorController(VacancyDAO vacancyDAO,
                                   EducationalInstitutionDAO educationDAO,
                                   SiteUserDAO siteUserDAO,
                                   RoleDAO roleDAO) {
        this.vacancyDAO = vacancyDAO;
        this.educationDAO = educationDAO;
        this.siteUserDAO = siteUserDAO;
        this.roleDAO = roleDAO;
    }

    @GetMapping("/edit/{id}")
    public String editVacancyForm(@PathVariable Long id, Model model,
                                  Authentication authentication) {
        Vacancy vacancy = vacancyDAO.getById(id);
        String login = authentication.getName();
        SiteUser company = siteUserDAO.getUserByLogin(login);

        if (vacancy == null || !vacancy.getCompany().getId().equals(company.getId())) {
            return "redirect:/employer/vacancies";
        }

        model.addAttribute("vacancy", vacancy);
        model.addAttribute("educations", educationDAO.getAll());
        model.addAttribute("companies", siteUserDAO.findByCriteria(
                roleDAO.getRoleByName("Работодатель"), null, null));

        return "employer/vacancyEditor";
    }

    @PostMapping("/save")
    public String saveVacancy(
            @RequestParam(required = false) Long id,
            @RequestParam String vacancyName,
            @RequestParam String description,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) Long educationId,
            @RequestParam(required = false) Long companyId,
            Authentication authentication) {

        String login = authentication.getName();
        SiteUser currentUser = siteUserDAO.getUserByLogin(login);
        SiteUser company = companyId != null ? siteUserDAO.getById(companyId) : currentUser;

        EducationalInstitution education = educationId != null ?
                educationDAO.getById(educationId) : null;

        Vacancy vacancy;
        if (id != null) {
            vacancy = vacancyDAO.getById(id);
            vacancy.setVacancyName(vacancyName);
            vacancy.setDescription(description);
            vacancy.setMinSalary(minSalary);
            vacancy.setEducation(education);
            vacancy.setCompany(company);
        } else {
            vacancy = new Vacancy(vacancyName, description, minSalary, company, education, 0);
        }

        vacancyDAO.save(vacancy);
        return "redirect:/employer/vacancies";
    }

    @PostMapping("/delete/{id}")
    public String deleteVacancy(@PathVariable Long id, Authentication authentication) {
        String login = authentication.getName();
        Vacancy vacancy = vacancyDAO.getById(id);
        SiteUser company = siteUserDAO.getUserByLogin(login);

        if (vacancy != null && vacancy.getCompany().getId().equals(company.getId())) {
            vacancyDAO.delete(vacancy);
        }

        return "redirect:/employer/vacancies";
    }
}