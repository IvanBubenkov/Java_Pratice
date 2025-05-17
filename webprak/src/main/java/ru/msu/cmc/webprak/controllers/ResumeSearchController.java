package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.EducationalInstitutionDAO;
import ru.msu.cmc.webprak.DAO.ResumeDAO;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.DAO.Work_historyDAO;
import ru.msu.cmc.webprak.models.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/resumes/search")
public class ResumeSearchController {

    @Autowired
    private ResumeDAO resumeDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private Work_historyDAO workHistoryDAO;

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;

    @GetMapping
    public String searchPage(
            @RequestParam(required = false) String resumeName,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long educationId,
            @RequestParam(required = false) String previousPosition,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            Model model) {

        // Получаем всех соискателей (обычных пользователей) для фильтра
        List<SiteUser> allCandidates = siteUserDAO.getAllUsers();

        // Получаем все возможные уровни образования
        List<EducationalInstitution> allEducationLevels = (List<EducationalInstitution>) educationalInstitutionDAO.getAll();

        // Получаем отфильтрованные резюме
        List<Resume> resumes = resumeDAO.findByCriteria(
                resumeName,
                userId,
                educationId != null ? educationalInstitutionDAO.getById(educationId) : null,
                previousPosition,
                minSalary,
                maxSalary
        );

        // Для каждого резюме получаем историю работы
        Map<Long, List<WorkHistory>> workHistoryMap = new HashMap<>();
        for (Resume resume : resumes) {
            List<WorkHistory> workHistory = workHistoryDAO.findByCriteria(
                    null, // vacancyName
                    resume.getUser(), // applicant
                    null, // company
                    null, // minSalary
                    null, // startDate
                    null  // endDate
            );
            workHistoryMap.put(resume.getId(), workHistory);
        }

        // Добавляем параметры в модель
        model.addAttribute("resumes", resumes);
        model.addAttribute("workHistoryMap", workHistoryMap);
        model.addAttribute("allCandidates", allCandidates);
        model.addAttribute("allEducationLevels", allEducationLevels);
        model.addAttribute("filterResumeName", resumeName);
        model.addAttribute("filterUserId", userId);
        model.addAttribute("filterEducationId", educationId);
        model.addAttribute("filterPreviousPosition", previousPosition);
        model.addAttribute("filterMinSalary", minSalary);
        model.addAttribute("filterMaxSalary", maxSalary);

        return "resumeSearch";
    }
}