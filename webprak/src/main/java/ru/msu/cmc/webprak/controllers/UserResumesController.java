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
@RequestMapping("/my-resumes")
public class UserResumesController {

    @Autowired
    private ResumeDAO resumeDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private Work_historyDAO workHistoryDAO;

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;

    @GetMapping
    public String userResumesPage(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Long educationId,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(required = false) String companyExperience,
            Model model) {

        SiteUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth";
        }

        List<Resume> allResumes = resumeDAO.findByCriteria(
                null,
                currentUser.getId(),
                null,
                null,
                null,
                null
        );

        List<Resume> filteredResumes = allResumes.stream()
                .filter(resume -> filterResume(resume, position, educationId, minSalary, maxSalary, companyExperience))
                .sorted(Comparator.comparing(Resume::getNumberOfViews).reversed())
                .collect(Collectors.toList());

        prepareModel(model, currentUser, filteredResumes, position, educationId, minSalary, maxSalary, companyExperience);

        return "userResumes";
    }

    @PostMapping("/delete/{id}")
    public String deleteResume(@PathVariable Long id) {
        SiteUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth";
        }

        Resume resume = resumeDAO.getById(id);
        if (resume != null && resume.getUser().getId().equals(currentUser.getId())) {
            resumeDAO.delete(resume);
        }

        return "redirect:/my-resumes";
    }

    @GetMapping("/new")
    public String createNewResume() {
        SiteUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth";
        }

        Resume newResume = new Resume();
        newResume.setUser(currentUser);
        newResume.setResumeName("Новое резюме");
        newResume.setNumberOfViews(0L);
        resumeDAO.save(newResume);

        return "redirect:/resumes/edit/" + newResume.getId();
    }

    private boolean filterResume(Resume resume, String position, Long educationId,
                                 BigDecimal minSalary, BigDecimal maxSalary, String companyExperience) {
        if (position != null && !position.isEmpty() &&
                (resume.getResumeName() == null || !resume.getResumeName().toLowerCase().contains(position.toLowerCase()))) {
            return false;
        }

        if (educationId != null && (resume.getUser().getEducation() == null ||
                !resume.getUser().getEducation().getId().equals(educationId))) {
            return false;
        }

        if (minSalary != null && resume.getMinSalaryRequirement() != null &&
                resume.getMinSalaryRequirement().compareTo(minSalary) < 0) {
            return false;
        }

        if (maxSalary != null && resume.getMinSalaryRequirement() != null &&
                resume.getMinSalaryRequirement().compareTo(maxSalary) > 0) {
            return false;
        }

        if (companyExperience != null && !companyExperience.isEmpty()) {
            List<WorkHistory> workHistory = workHistoryDAO.findByCriteria(
                    null, resume.getUser(), null, null, null, null);
            boolean hasCompanyExperience = workHistory.stream()
                    .anyMatch(wh -> wh.getCompany() != null &&
                            wh.getCompany().getFullNameCompany() != null &&
                            wh.getCompany().getFullNameCompany().toLowerCase()
                                    .contains(companyExperience.toLowerCase()));
            if (!hasCompanyExperience) {
                return false;
            }
        }

        return true;
    }

    private void prepareModel(Model model, SiteUser currentUser, List<Resume> filteredResumes,
                              String position, Long educationId, BigDecimal minSalary,
                              BigDecimal maxSalary, String companyExperience) {
        List<WorkHistory> userWorkHistory = workHistoryDAO.findByCriteria(
                null, currentUser, null, null, null, null);

        Set<String> availableCompanies = userWorkHistory.stream()
                .filter(wh -> wh.getCompany() != null)
                .map(wh -> wh.getCompany().getFullNameCompany())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> availablePositions = filteredResumes.stream()
                .map(Resume::getResumeName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, List<WorkHistory>> workHistoryMap = new HashMap<>();
        for (Resume resume : filteredResumes) {
            List<WorkHistory> workHistory = workHistoryDAO.findByCriteria(
                    null, resume.getUser(), null, null, null, null);
            workHistoryMap.put(resume.getId(), workHistory);
        }

        model.addAttribute("resumes", filteredResumes);
        model.addAttribute("workHistoryMap", workHistoryMap);
        model.addAttribute("allEducationLevels", educationalInstitutionDAO.getAll());
        model.addAttribute("availableCompanies", availableCompanies);
        model.addAttribute("availablePositions", availablePositions);
        model.addAttribute("filterPosition", position);
        model.addAttribute("filterEducationId", educationId);
        model.addAttribute("filterMinSalary", minSalary);
        model.addAttribute("filterMaxSalary", maxSalary);
        model.addAttribute("filterCompanyExperience", companyExperience);
    }

    private SiteUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return siteUserDAO.getUserByLogin(authentication.getName());
        }
        return null;
    }
}