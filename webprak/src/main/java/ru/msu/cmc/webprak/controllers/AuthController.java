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

import java.util.List;

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
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model,
            HttpServletRequest request) {

        System.out.println("[DEBUG] Текущий URL: " + request.getRequestURL() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : ""));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            System.out.println("[DEBUG] Пользователь уже авторизован: " + auth.getName());
            return determineRedirectUrl(siteUserDAO.getUserByLogin(auth.getName()));
        }

        if (error != null) {
            System.out.println("[DEBUG] Обнаружен параметр error. Источник: " + getErrorSource(request));
            model.addAttribute("error", "Неверный логин или пароль");
        }

        if (logout != null) {
            System.out.println("[DEBUG] Обнаружен параметр logout");
            model.addAttribute("success", "Вы успешно вышли из системы");
        }

        // Передаём список ролей в шаблон для выпадающего списка
        List<Role> roles = (List<Role>) roleDAO.getAll();
        model.addAttribute("roles", roles);

        return "auth";
    }

    private String getErrorSource(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/login")) {
            return "Форма входа";
        } else if (request.getRequestURI().contains("/error")) {
            return "Система обработки ошибок";
        }
        return "Неизвестный источник";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String login,
                            @RequestParam String password,
                            Model model,
                            HttpServletRequest request) {

        model.addAttribute("debugInfo", "Обработка входа для пользователя: " + login);

        SiteUser user = siteUserDAO.getUserByLogin(login);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Неверный логин или пароль");
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
                               @RequestParam("homeAddress") String address,  // получили homeAddress из формы
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

        UserStatus defaultStatus = userStatusDAO.getById(1L); // Например, "Активный"

        SiteUser newUser = new SiteUser(
                login,
                passwordEncoder.encode(password),
                role,
                defaultStatus,
                fullNameCompany,
                email,
                address // передаём в конструктор
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
        if (roleName.contains("ADMIN")) {
            return "redirect:/adminPanel";
        } else if (roleName.contains("COMPANY") || roleName.contains("USER")) {
            return "redirect:/profile";
        }

        return "redirect:/error";
    }
}
