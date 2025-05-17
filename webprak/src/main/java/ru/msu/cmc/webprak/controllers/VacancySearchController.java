package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.EducationalInstitutionDAO;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.DAO.VacancyDAO;
import ru.msu.cmc.webprak.models.EducationalInstitution;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.Vacancy;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/vacancies/search")
public class VacancySearchController {

    @Autowired
    private VacancyDAO vacancyDAO;

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @GetMapping
    public String searchPage(
            @RequestParam(required = false) String vacancyName,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) Long educationId,
            Model model) {

        // Получаем все компании (только с ролью COMPANY) и учебные заведения
        List<SiteUser> allCompanies = siteUserDAO.getAllCompanies();
        List<EducationalInstitution> allEducations = (List<EducationalInstitution>) educationalInstitutionDAO.getAll();

        // Получаем отфильтрованные вакансии
        EducationalInstitution education = educationId != null ?
                educationalInstitutionDAO.getById(educationId) : null;
        SiteUser company = companyId != null ? siteUserDAO.getById(companyId) : null;

        List<Vacancy> vacancies = vacancyDAO.findByCriteria(
                vacancyName,
                companyId,
                minSalary,
                education
        );

        // Добавляем параметры в модель
        model.addAttribute("vacancies", vacancies);
        model.addAttribute("allCompanies", allCompanies);
        model.addAttribute("allEducations", allEducations);
        model.addAttribute("filterVacancyName", vacancyName);
        model.addAttribute("filterCompanyId", companyId);
        model.addAttribute("filterMinSalary", minSalary);
        model.addAttribute("filterEducationId", educationId);

        return "vacancySearch";
    }
}