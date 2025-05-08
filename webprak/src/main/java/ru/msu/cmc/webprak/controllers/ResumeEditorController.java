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
@RequestMapping("/applicant/resume")
public class ResumeEditorController {
    private final ResumeDAO resumeDAO;
    private final SiteUserDAO siteUserDAO;

    @Autowired
    public ResumeEditorController(ResumeDAO resumeDAO, SiteUserDAO siteUserDAO) {
        this.resumeDAO = resumeDAO;
        this.siteUserDAO = siteUserDAO;
    }

    @GetMapping("/edit/{id}")
    public String editResumeForm(@PathVariable Long id, Model model,
                                 Authentication authentication) {
        Resume resume = resumeDAO.getById(id);
        String login = authentication.getName();
        SiteUser applicant = siteUserDAO.getUserByLogin(login);

        if (resume == null || !resume.getUser().getId().equals(applicant.getId())) {
            return "redirect:/applicant/resumes";
        }

        model.addAttribute("resume", resume);
        return "applicant/resumeEditor";
    }

    @PostMapping("/save")
    public String saveResume(
            @RequestParam(required = false) Long id,
            @RequestParam String resumeName,
            @RequestParam(required = false) BigDecimal minSalaryRequirement,
            Authentication authentication) {

        String login = authentication.getName();
        SiteUser applicant = siteUserDAO.getUserByLogin(login);

        Resume resume;
        if (id != null) {
            resume = resumeDAO.getById(id);
            resume.setResumeName(resumeName);
            resume.setMinSalaryRequirement(minSalaryRequirement);
        } else {
            resume = new Resume(applicant, minSalaryRequirement, 0L, resumeName);
        }

        resumeDAO.save(resume);
        return "redirect:/applicant/resumes";
    }

    @PostMapping("/delete/{id}")
    public String deleteResume(@PathVariable Long id, Authentication authentication) {
        String login = authentication.getName();
        Resume resume = resumeDAO.getById(id);
        SiteUser applicant = siteUserDAO.getUserByLogin(login);

        if (resume != null && resume.getUser().getId().equals(applicant.getId())) {
            resumeDAO.delete(resume);
        }

        return "redirect:/applicant/resumes";
    }
}