package ru.msu.cmc.webprak.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Здесь должна быть логика получения данных пользователя из БД
        // Для примера используем заглушки
        model.addAttribute("fullName", "Иванов Иван Иванович");
        model.addAttribute("email", email);
        model.addAttribute("address", "г. Москва, ул. Примерная, д. 1");
        model.addAttribute("jobSeekerStatus", "Активно ищу работу");
        model.addAttribute("isCompany", false); // или true для компании

        return "profile";
    }
}