package ru.msu.cmc.webprak.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class WorkHistoryDAOImplTest {

    @Autowired
    private Work_historyDAO workHistoryDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserStatusDAO userStatusDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private SiteUser applicant;
    private SiteUser company;
    private WorkHistory workHistory1;
    private WorkHistory workHistory2;

    @Test
    @DisplayName("Test complex work history search")
    void testFindByCriteria() {
        // Test applicant filter
        List<WorkHistory> result = workHistoryDAO.findByCriteria(
                null, applicant, null, null, null, null
        );
        assertEquals(2, result.size());
    }

    @BeforeEach
    void setUp() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Role employerRole = new Role();
            employerRole.setRoleName("Работодатель");
            roleDAO.save(employerRole);

            Role applicantRole = new Role();
            applicantRole.setRoleName("Соискатель");
            roleDAO.save(applicantRole);

            UserStatus activeStatus = new UserStatus();
            activeStatus.setStatusName("Активен");
            userStatusDAO.save(activeStatus);

            company = new SiteUser();
            company.setLogin("company1");
            company.setPassword("pass");
            company.setRole(employerRole);
            company.setStatus(activeStatus);
            company.setFullNameCompany("Tech Corp");
            company.setEmail("tech@corp.com");
            company.setHomeAddress("Москва");
            siteUserDAO.save(company);

            applicant = new SiteUser();
            applicant.setLogin("applicant1");
            applicant.setPassword("pass");
            applicant.setRole(applicantRole);
            applicant.setStatus(activeStatus);
            applicant.setFullNameCompany("Иван Иванов");
            applicant.setEmail("ivan@mail.ru");
            applicant.setHomeAddress("СПб");
            siteUserDAO.save(applicant);

            workHistory1 = new WorkHistory();
            workHistory1.setVacancyName("Java Разработчик");
            workHistory1.setApplicant(applicant);
            workHistory1.setCompany(company);
            workHistory1.setSalary(160000L);
            workHistory1.setDateStart(LocalDate.of(2021, 1, 1));
            workHistory1.setDateEnd(LocalDate.of(2023, 1, 1));

            workHistory2 = new WorkHistory();
            workHistory2.setVacancyName("Менеджер проектов");
            workHistory2.setApplicant(applicant);
            workHistory2.setCompany(company);
            workHistory2.setSalary(90000L);
            workHistory2.setDateStart(LocalDate.of(2022, 6, 1));
            workHistory2.setDateEnd(LocalDate.of(2023, 6, 1));

            workHistoryDAO.save(workHistory1);
            workHistoryDAO.save(workHistory2);

            session.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE work_history RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE site_user RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE roles RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE user_statuses RESTART IDENTITY CASCADE").executeUpdate();
            session.getTransaction().commit();
        }
    }
}