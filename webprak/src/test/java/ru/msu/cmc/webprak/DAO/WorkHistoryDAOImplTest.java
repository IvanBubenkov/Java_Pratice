package ru.msu.cmc.webprak.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.webprak.models.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class WorkHistoryDAOImplTest {

    @Autowired
    private Work_historyDAO workHistoryDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserStatusDAO userStatusDAO;

    private SiteUser applicant;
    private SiteUser company;
    private WorkHistory workHistory1;
    private WorkHistory workHistory2;

    @BeforeEach
    void setUp() {
        // Очистка через DAO
        workHistoryDAO.getAll().forEach(workHistoryDAO::delete);
        siteUserDAO.getAll().forEach(siteUserDAO::delete);
        roleDAO.getAll().forEach(roleDAO::delete);
        userStatusDAO.getAll().forEach(userStatusDAO::delete);

        Role employerRole = new Role("Работодатель");
        roleDAO.save(employerRole);

        Role applicantRole = new Role("Соискатель");
        roleDAO.save(applicantRole);

        UserStatus activeStatus = new UserStatus("Активен");
        userStatusDAO.save(activeStatus);

        company = new SiteUser(
                "company1",
                "pass",
                employerRole,
                activeStatus,
                "Tech Corp",
                "tech@corp.com",
                "Москва"
        );
        siteUserDAO.save(company);

        applicant = new SiteUser(
                "applicant1",
                "pass",
                applicantRole,
                activeStatus,
                "Иван Иванов",
                "ivan@mail.ru",
                "СПб"
        );
        siteUserDAO.save(applicant);

        workHistory1 = new WorkHistory();
        workHistory1.setVacancyName("Java Разработчик");
        workHistory1.setApplicant(applicant);
        workHistory1.setCompany(company);
        workHistory1.setSalary(160000L);
        workHistory1.setDateStart(LocalDate.of(2021, 1, 1));
        workHistory1.setDateEnd(LocalDate.of(2023, 1, 1));
        workHistoryDAO.save(workHistory1);

        workHistory2 = new WorkHistory();
        workHistory2.setVacancyName("Менеджер проектов");
        workHistory2.setApplicant(applicant);
        workHistory2.setCompany(company);
        workHistory2.setSalary(90000L);
        workHistory2.setDateStart(LocalDate.of(2022, 6, 1));
        workHistory2.setDateEnd(LocalDate.of(2023, 6, 1));
        workHistoryDAO.save(workHistory2);
    }

    // Все тестовые методы остаются без изменений
    // (как в предыдущем ответе с @Test методами)

    @Test
    @DisplayName("Test complex criteria with dates")
    void testDateRangeWithBoundaries() {
        List<WorkHistory> result = workHistoryDAO.findByCriteria(
                null,
                null,
                null,
                null,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 6, 1)
        );
        assertEquals(0, result.size());
    }
}