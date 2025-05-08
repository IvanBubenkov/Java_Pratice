package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.*;
import ru.msu.cmc.webprak.models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @PersistenceContext
    private EntityManager entityManager;

    private final SiteUserDAO siteUserDAO;

    @Autowired
    public AdminController(SiteUserDAO siteUserDAO) {
        this.siteUserDAO = siteUserDAO;
    }

    @GetMapping("")
    public String adminPanel(Model model) {
        return "admin/adminPanel";
    }

    @PostMapping("/execute")
    public String executeSql(@RequestParam String sqlCommand, Model model) {
        try {
            Query query = entityManager.createNativeQuery(sqlCommand);

            if (sqlCommand.trim().toLowerCase().startsWith("select")) {
                List<?> result = query.getResultList();
                model.addAttribute("result", result);
            } else {
                int updateCount = query.executeUpdate();
                model.addAttribute("message", "Команда выполнена. Затронуто строк: " + updateCount);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка выполнения: " + e.getMessage());
        }

        return "admin/adminPanel";
    }
}