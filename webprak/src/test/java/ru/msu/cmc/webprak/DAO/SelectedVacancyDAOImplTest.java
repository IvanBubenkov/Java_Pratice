package ru.msu.cmc.webprak.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class SelectedVacancyDAOImplTest {

    @Autowired
    private SelectedVacancyDAO selectedVacancyDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private VacancyDAO vacancyDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserStatusDAO userStatusDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private SiteUser applicant1;
    private Vacancy vacancy1;
    private Vacancy vacancy2;

    @Test
    @DisplayName("Test finding selected vacancies by applicant ID")
    void testFindByApplicantId() {
        List<SelectedVacancy> result = selectedVacancyDAO.findByApplicantId(applicant1.getId());
        assertEquals(2, result.size());

        List<SelectedVacancy> emptyResult = selectedVacancyDAO.findByApplicantId(999L);
        assertTrue(emptyResult.isEmpty());
    }

    @BeforeEach
    void setUp() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Role employerRole = new Role("Работодатель");
            Role applicantRole = new Role("Соискатель");
            UserStatus activeStatus = new UserStatus("Активен");

            roleDAO.save(employerRole);
            roleDAO.save(applicantRole);
            userStatusDAO.save(activeStatus);

            SiteUser company = new SiteUser();
            company.setLogin("company1");
            company.setPassword("pass");
            company.setRole(employerRole);
            company.setStatus(activeStatus);
            company.setFullNameCompany("Tech Corp");
            company.setEmail("tech@corp.com");
            company.setHomeAddress("Москва");
            siteUserDAO.save(company);

            applicant1 = new SiteUser();
            applicant1.setLogin("applicant1");
            applicant1.setPassword("pass");
            applicant1.setRole(applicantRole);
            applicant1.setStatus(activeStatus);
            applicant1.setFullNameCompany("Иван Иванов");
            applicant1.setEmail("ivan@mail.ru");
            applicant1.setHomeAddress("СПб");
            siteUserDAO.save(applicant1);

            vacancy1 = new Vacancy();
            vacancy1.setVacancyName("Java Developer");
            vacancy1.setCompany(company);
            vacancy1.setDescription("Backend development");
            vacancy1.setMinSalary(BigDecimal.valueOf(150000));

            vacancy2 = new Vacancy();
            vacancy2.setVacancyName("DevOps Engineer");
            vacancy2.setCompany(company);
            vacancy2.setDescription("Cloud infrastructure");
            vacancy2.setMinSalary(BigDecimal.valueOf(180000));

            vacancyDAO.save(vacancy1);
            vacancyDAO.save(vacancy2);

            SelectedVacancy sv1 = new SelectedVacancy();
            sv1.setVacancy(vacancy1);
            sv1.setApplicant(applicant1);

            SelectedVacancy sv2 = new SelectedVacancy();
            sv2.setVacancy(vacancy2);
            sv2.setApplicant(applicant1);

            selectedVacancyDAO.save(sv1);
            selectedVacancyDAO.save(sv2);

            session.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE selected_vacancies RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE vacancy RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE site_user RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE roles RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE user_statuses RESTART IDENTITY CASCADE").executeUpdate();
            session.getTransaction().commit();
        }
    }
}