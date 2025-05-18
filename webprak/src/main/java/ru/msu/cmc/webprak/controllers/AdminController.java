package ru.msu.cmc.webprak.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
        if (!isAdmin(authentication)) {
            return "redirect:/auth";
        }
        return "adminPanel";
    }

    @Transactional
    @PostMapping("/execute")
    public String executeSql(@RequestParam String sqlCommand,
                             Model model,
                             Authentication authentication) {
        if (!isAdmin(authentication)) {
            return "redirect:/auth";
        }

        try {
            // Запрещаем DDL-операции и потенциально опасные команды
            if (isDangerousQuery(sqlCommand)) {
                model.addAttribute("error", "Выполнение этого типа запроса запрещено");
                return "adminPanel";
            }

            Query query = entityManager.createNativeQuery(sqlCommand);

            if (isSelectQuery(sqlCommand)) {
                List<Map<String, Object>> result = query.unwrap(org.hibernate.query.NativeQuery.class)
                        .setResultTransformer(org.hibernate.transform.Transformers.ALIAS_TO_ENTITY_MAP)
                        .getResultList();
                model.addAttribute("result", result);
            } else {
                int updateCount = query.executeUpdate();
                model.addAttribute("message", "Успешно. Затронуто строк: " + updateCount);

                // Явное сброс кэша для измененных сущностей
                entityManager.flush();
                entityManager.clear();
            }
        } catch (Exception e) {
            model.addAttribute("error", "SQL ошибка: " + e.getMessage());
            // Откат транзакции произойдет автоматически благодаря @Transactional
        }

        return "adminPanel";
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        SiteUser user = siteUserDAO.getUserByLogin(authentication.getName());
        return user != null && "ADMIN".equals(user.getRole().getRoleName());
    }

    private boolean isSelectQuery(String sql) {
        return sql.trim().toLowerCase().startsWith("select");
    }

    private boolean isDangerousQuery(String sql) {
        String lowerSql = sql.trim().toLowerCase();
        return lowerSql.startsWith("drop") ||
                lowerSql.startsWith("alter") ||
                lowerSql.startsWith("truncate") ||
                lowerSql.startsWith("create") ||
                lowerSql.startsWith("grant");
    }
}