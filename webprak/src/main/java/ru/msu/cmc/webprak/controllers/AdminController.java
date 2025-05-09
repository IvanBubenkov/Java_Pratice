package ru.msu.cmc.webprak.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.models.SiteUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @GetMapping("/panel")
    public String adminPanel(Model model, Authentication authentication) {
        // Проверка прав администратора
        if (!isAdmin(authentication)) {
            return "redirect:/auth";
        }

        // Возвращаем имя шаблона без поддиректории
        return "adminPanel";
    }

    @PostMapping("/execute")
    public String executeSql(@RequestParam String sqlCommand,
                             Model model,
                             Authentication authentication) {
        // Проверка прав администратора
        if (!isAdmin(authentication)) {
            return "redirect:/auth";
        }

        try {
            Query query = entityManager.createNativeQuery(sqlCommand);

            // Обработка SELECT запросов
            if (isSelectQuery(sqlCommand)) {
                List<Map<String, Object>> result = query.unwrap(org.hibernate.query.NativeQuery.class)
                        .setResultTransformer(org.hibernate.transform.Transformers.ALIAS_TO_ENTITY_MAP)
                        .getResultList();
                model.addAttribute("result", result);
            }
            // Обработка других SQL команд
            else {
                int updateCount = query.executeUpdate();
                model.addAttribute("message", "Успешно. Затронуто строк: " + updateCount);
            }
        } catch (Exception e) {
            model.addAttribute("error", "SQL ошибка: " + e.getMessage());
        }

        // Возвращаем имя шаблона без поддиректории
        return "adminPanel";
    }

    /**
     * Проверяет, является ли пользователь администратором
     */
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        SiteUser user = siteUserDAO.getUserByLogin(authentication.getName());
        return user != null && "ADMIN".equals(user.getRole().getRoleName());
    }

    /**
     * Проверяет, является ли запрос SELECT-запросом
     */
    private boolean isSelectQuery(String sql) {
        return sql.trim().toLowerCase().startsWith("select");
    }
}