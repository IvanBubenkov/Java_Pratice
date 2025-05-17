package ru.msu.cmc.webprak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.DAO.RoleDAO;
import ru.msu.cmc.webprak.DAO.Work_historyDAO;
import ru.msu.cmc.webprak.models.Role;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.WorkHistory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/work-history")
public class WorkHistoryController {

    @Autowired
    private Work_historyDAO workHistoryDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private RoleDAO roleDAO;

    @GetMapping
    public String workHistoryPage(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String vacancyName,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {

        SiteUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth";
        }

        // Парсим даты из строковых параметров
        LocalDate parsedStartDate = parseDate(startDate);
        LocalDate parsedEndDate = parseDate(endDate);

        // Получаем все записи для текущего пользователя
        List<WorkHistory> records = workHistoryDAO.findByCriteria(
                vacancyName,
                currentUser,
                null, // Компанию будем фильтровать отдельно
                minSalary,
                parsedStartDate,
                parsedEndDate
        );

        // Дополнительная фильтрация по названию компании (если указано)
        if (companyName != null && !companyName.isEmpty()) {
            records = records.stream()
                    .filter(record -> record.getCompany() != null &&
                            record.getCompany().getFullNameCompany() != null &&
                            record.getCompany().getFullNameCompany().contains(companyName))
                    .collect(Collectors.toList());
        }

        model.addAttribute("records", records);
        model.addAttribute("filterCompanyName", companyName);
        model.addAttribute("filterVacancyName", vacancyName);
        model.addAttribute("filterMinSalary", minSalary);
        model.addAttribute("filterStartDate", startDate);
        model.addAttribute("filterEndDate", endDate);

        return "workHistory";
    }

    @PostMapping("/delete/{id}")
    public String deleteRecord(@PathVariable Long id) {
        SiteUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth";
        }

        WorkHistory record = workHistoryDAO.getById(id);
        if (record != null && record.getApplicant().getId().equals(currentUser.getId())) {
            workHistoryDAO.delete(record);
        }
        return "redirect:/work-history";
    }

    private SiteUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return siteUserDAO.getUserByLogin(authentication.getName());
        }
        return null;
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }
    @GetMapping("/new")
    public String newRecordForm(@RequestParam(required = false) String errorMessage, Model model) {
        SiteUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth";
        }

        // Получаем роль COMPANY
        Role companyRole = roleDAO.getRoleByName("COMPANY");
        if (companyRole == null) {
            throw new IllegalStateException("Роль COMPANY не найдена");
        }

        // Получаем список всех компаний
        List<SiteUser> companies = siteUserDAO.findByCriteria(companyRole, null, null);

        model.addAttribute("companies", companies);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
        return "workHistoryForm";
    }

    @PostMapping("/save")
    public String saveRecord(
            @RequestParam("companyId") Long companyId,
            @RequestParam("vacancyName") String vacancyName,
            @RequestParam("salary") Long salary,
            @RequestParam("dateStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,
            @RequestParam(value = "dateEnd", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd,
            RedirectAttributes redirectAttributes) {

        try {
            SiteUser currentUser = getCurrentUser();
            if (currentUser == null) {
                return "redirect:/auth";
            }

            // Получаем компанию по ID
            SiteUser company = siteUserDAO.getById(companyId);
            if (company == null) {
                redirectAttributes.addAttribute("errorMessage", "Компания не найдена");
                return "redirect:/work-history/new";
            }

            // Валидация
            if (vacancyName == null || vacancyName.trim().isEmpty()) {
                redirectAttributes.addAttribute("errorMessage", "Укажите должность");
                return "redirect:/work-history/new";
            }

            if (salary == null || salary <= 0) {
                redirectAttributes.addAttribute("errorMessage", "Укажите корректную зарплату");
                return "redirect:/work-history/new";
            }

            if (dateStart == null) {
                redirectAttributes.addAttribute("errorMessage", "Укажите дату начала");
                return "redirect:/work-history/new";
            }

            // Создаем новую запись
            WorkHistory record = new WorkHistory();
            record.setVacancyName(vacancyName);
            record.setApplicant(currentUser);
            record.setCompany(company);
            record.setSalary(salary);
            record.setDateStart(dateStart);
            record.setDateEnd(dateEnd);

            // Сохраняем запись
            workHistoryDAO.save(record);

            return "redirect:/work-history";
        } catch (Exception e) {
            redirectAttributes.addAttribute("errorMessage", "Ошибка при сохранении: " + e.getMessage());
            return "redirect:/work-history/new";
        }
    }
}