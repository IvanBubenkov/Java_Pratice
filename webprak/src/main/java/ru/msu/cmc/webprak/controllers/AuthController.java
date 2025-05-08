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

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserStatusDAO userStatusDAO;

    @GetMapping
    public String authPage(@RequestParam(required = false) String error,
                           @RequestParam(required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверный логин или пароль");
        }
        if (logout != null) {
            model.addAttribute("success", "Вы успешно вышли из системы");
        }
        return "auth";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String fullNameCompany,
            @RequestParam String email,
            @RequestParam Integer userType,
            Model model) {

        System.out.println("Попытка регистрации: " + login);

        if (siteUserDAO.getUserByLogin(login) != null) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "redirect:/auth#register";
        }

        Role role = userType == 2 ? roleDAO.getById(2L) : roleDAO.getById(1L);
        UserStatus status = userStatusDAO.getById(1L);

        SiteUser newUser = new SiteUser(
                login,
                password,
                role,
                status,
                fullNameCompany,
                email,
                null
        );

        siteUserDAO.save(newUser);
        model.addAttribute("success", "Регистрация прошла успешно! Теперь вы можете войти.");
        return "redirect:/auth";
    }
}