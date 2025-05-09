package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.RoleDAO;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.DAO.UserStatusDAO;
import ru.msu.cmc.webprak.models.Role;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserStatusDAO userStatusDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String authPage(
            @RequestParam(required = false) String error,  // Параметр error из URL (?error=...)
            @RequestParam(required = false) String logout,  // Параметр logout из URL (?logout=...)
            Model model,
            HttpServletRequest request) {  // Добавляем для просмотра полного URL

        // Выводим полный URL запроса для отладки
        System.out.println("[DEBUG] Текущий URL: " + request.getRequestURL() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : ""));

        // Проверяем, есть ли уже аутентифицированный пользователь
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            System.out.println("[DEBUG] Пользователь уже авторизован: " + auth.getName());
            return determineRedirectUrl(siteUserDAO.getUserByLogin(auth.getName()));
        }

        // Обработка параметра error
        if (error != null) {
            System.out.println("[DEBUG] Обнаружен параметр error. Источник: " + getErrorSource(request));
            System.out.println("[DEBUG] Значение error: " + error);
            model.addAttribute("error", "Неверный логин или пароль%%%");
        }

        // Обработка параметра logout
        if (logout != null) {
            System.out.println("[DEBUG] Обнаружен параметр logout");
            model.addAttribute("success", "Вы успешно вышли из системы");
        }

        return "auth";
    }

    // Вспомогательный метод для определения источника ошибки
    private String getErrorSource(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/login")) {
            return "Форма входа";
        } else if (request.getRequestURI().contains("/error")) {
            return "Система обработки ошибок";
        }
        return "Неизвестный источник (возможно, прямой URL или редирект)";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String login,
                            @RequestParam String password,
                            Model model,
                            HttpServletRequest request) {

        // Добавляем отладочное сообщение
        model.addAttribute("debugInfo", "Обработка входа для пользователя: " + login);

        SiteUser user = siteUserDAO.getUserByLogin(login);

        if (user == null) {
            model.addAttribute("error", "Неверный логин или пароль");
            model.addAttribute("debugPasswordAttempt", "Введённый пароль: " + password); // Осторожно! Только для отладки!
            return "auth";
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Неверный пароль");
            model.addAttribute("debugStoredHash", "Хэш пароля из БД: " + user.getPassword());
            return "auth";
        }

        return determineRedirectUrl(user);
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String login,
                               @RequestParam String password,
                               @RequestParam String fullNameCompany,
                               @RequestParam String email,
                               @RequestParam Integer roleId,
                               Model model) {

        if (siteUserDAO.getUserByLogin(login) != null) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "redirect:/auth#register";
        }

        Role role = roleDAO.getById(roleId.longValue());
        if (role == null) {
            model.addAttribute("error", "Указана несуществующая роль");
            return "redirect:/auth#register";
        }

        UserStatus defaultStatus = userStatusDAO.getById(1L);

        SiteUser newUser = new SiteUser(
                login,
                passwordEncoder.encode(password),
                role,
                defaultStatus,
                fullNameCompany,
                email,
                null
        );

        siteUserDAO.save(newUser);
        model.addAttribute("success", "Регистрация прошла успешно!");
        return "redirect:/auth";
    }

    private String determineRedirectUrl(SiteUser user) {
        if (user == null || user.getRole() == null) {
            return "redirect:/error";
        }

        String roleName = user.getRole().getRoleName().toUpperCase();

        if ("Администратор".contains(roleName)) {
            return "redirect:/adminPanel";
        } else if ("Работодатель".contains(roleName)) {
            return "redirect:/profile";
        } else if ("Соискатель".contains(roleName)) {
            return "redirect:/mainPage";
        }

        return "redirect:/mainPage";
    }
}