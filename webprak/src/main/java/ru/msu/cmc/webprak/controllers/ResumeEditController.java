package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.*;
import ru.msu.cmc.webprak.models.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/resumes")
public class ResumeEditController {

    @Autowired
    private ResumeDAO resumeDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private Work_historyDAO workHistoryDAO;

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;

    @GetMapping("/edit/{id}")
    public String editResumePage(@PathVariable Long id, Model model) {
        Resume resume = resumeDAO.getById(id);
        if (resume == null) {
            return "redirect:/my-resumes";
        }

        SiteUser user = resume.getUser();
        List<WorkHistory> workHistory = workHistoryDAO.findByCriteria(null, user, null, null, null, null);
        List<EducationalInstitution> allEducation = (List<EducationalInstitution>) educationalInstitutionDAO.getAll();

        model.addAttribute("resume", resume);
        model.addAttribute("workHistory", workHistory);
        model.addAttribute("allEducation", allEducation);
        return "editResume";
    }

    @PostMapping("/update/{id}")
    public String updateResume(
            @PathVariable Long id,
            @RequestParam String resumeName,
            @RequestParam String fullName,
            @RequestParam Long educationId,
            @RequestParam BigDecimal minSalary,
            Model model) {

        Resume resume = resumeDAO.getById(id);
        if (resume == null) {
            return "redirect:/my-resumes";
        }

        BigDecimal maxSalary = new BigDecimal("99999999.99");
        if (minSalary.compareTo(maxSalary) > 0) {
            model.addAttribute("error", "Максимальная зарплата не может превышать 99,999,999.99");
            model.addAttribute("resume", resume);
            model.addAttribute("workHistory", workHistoryDAO.findByCriteria(null, resume.getUser(), null, null, null, null));
            model.addAttribute("allEducation", (List<EducationalInstitution>) educationalInstitutionDAO.getAll());
            return "editResume";
        }

        resume.setResumeName(resumeName);
        resume.setMinSalaryRequirement(minSalary);

        SiteUser user = resume.getUser();
        user.setFullNameCompany(fullName);

        EducationalInstitution education = educationalInstitutionDAO.getById(educationId);
        user.setEducation(education);

        siteUserDAO.update(user);
        resumeDAO.update(resume);

        return "redirect:/my-resumes";
    }
}