package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.msu.cmc.webprak.DAO.*;
import ru.msu.cmc.webprak.models.*;

@Controller
@RequestMapping("/profile/edit")
public class ProfileEditorController {
    private final SiteUserDAO siteUserDAO;
    private final UserStatusDAO userStatusDAO;
    private final EducationalInstitutionDAO educationalInstitutionDAO; // Добавим DAO для образовательных учреждений

    @Autowired
    public ProfileEditorController(SiteUserDAO siteUserDAO,
                                   UserStatusDAO userStatusDAO,
                                   EducationalInstitutionDAO educationalInstitutionDAO) {
        this.siteUserDAO = siteUserDAO;
        this.userStatusDAO = userStatusDAO;
        this.educationalInstitutionDAO = educationalInstitutionDAO; // Инициализация
    }

    @GetMapping("")
    public String editProfileForm(Model model, Authentication authentication) {
        String login = authentication.getName();
        SiteUser user = siteUserDAO.getUserByLogin(login);

        model.addAttribute("user", user);

        // Получаем список всех статусов
        if (user.getRole().getRoleName().equals("USER")) {
            // Для обычных пользователей статусы "Ищет работу" и "Не ищет работу"
            model.addAttribute("statuses", userStatusDAO.getUserStatuses());
        } else if (user.getRole().getRoleName().equals("COMPANY")) {
            // Для работодателей только один статус "Работодатель"
            model.addAttribute("statuses", userStatusDAO.getCompanyStatus());
        }

        model.addAttribute("educations", educationalInstitutionDAO.getAll());
        return "profileEditor";
    }

    @PostMapping("")
    public String saveProfile(
            @RequestParam String fullNameCompany,
            @RequestParam String email,
            @RequestParam String homeAddress,
            @RequestParam Long statusId,
            @RequestParam String educationId, // Получаем id выбранного образования
            Authentication authentication) {

        String login = authentication.getName();
        SiteUser user = siteUserDAO.getUserByLogin(login);
        UserStatus status = userStatusDAO.getById(statusId);

        // Если выбрано "Без образования" (educationId = ""), то устанавливаем NULL
        if (educationId.isEmpty()) {
            user.setEducation(null); // Убираем информацию об образовании
        } else {
            EducationalInstitution education = educationalInstitutionDAO.getById(Long.parseLong(educationId)); // Получаем выбранное образование
            user.setEducation(education); // Устанавливаем образование
        }

        user.setFullNameCompany(fullNameCompany);
        user.setEmail(email);
        user.setHomeAddress(homeAddress);
        user.setStatus(status);

        siteUserDAO.save(user);

        return "redirect:/profile";
    }


}

