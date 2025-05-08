package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.msu.cmc.webprak.DAO.ResumeDAO;
import ru.msu.cmc.webprak.DAO.VacancyDAO;
import ru.msu.cmc.webprak.models.Resume;
import ru.msu.cmc.webprak.models.Vacancy;

import java.util.List;

@Controller
public class MainPageController {

    private final ResumeDAO resumeDAO;
    private final VacancyDAO vacancyDAO;

    @Autowired
    public MainPageController(ResumeDAO resumeDAO, VacancyDAO vacancyDAO) {
        this.resumeDAO = resumeDAO;
        this.vacancyDAO = vacancyDAO;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        List<Vacancy> topVacancies = vacancyDAO.findTopVacanciesByViews();
        List<Resume> topResumes = resumeDAO.findTopResumesByViews();

        model.addAttribute("topVacancies", topVacancies);
        model.addAttribute("topResumes", topResumes);

        return "mainPage";
    }
}
