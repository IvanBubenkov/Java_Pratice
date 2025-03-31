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
public class VacancyDAOTest {

    @Autowired
    private VacancyDAO vacancyDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserStatusDAO userStatusDAO;

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Role employerRole;
    private UserStatus activeStatus;
    private EducationalInstitution university;
    private SiteUser companyUser1;
    private SiteUser companyUser2;

    @Test
    @DisplayName("Test findByCriteria() method")
    void testFindByCriteria() {
        // 1. Поиск по названию вакансии
        List<Vacancy> result1 = vacancyDAO.findByCriteria("Разработчик", null, null, null);
        assertEquals(2, result1.size());

        // 2. Поиск по компании
        List<Vacancy> result2 = vacancyDAO.findByCriteria(null, companyUser2.getId(), null, null);
        assertEquals(1, result2.size());
        assertEquals("Менеджер проектов", result2.get(0).getVacancyName());

        // 3. Поиск по минимальной зарплате
        List<Vacancy> result3 = vacancyDAO.findByCriteria(null, null, BigDecimal.valueOf(150000), null);
        assertEquals(1, result3.size());
        assertEquals("Senior Python Разработчик", result3.get(0).getVacancyName());

        // 4. Комбинированный поиск
        List<Vacancy> result4 = vacancyDAO.findByCriteria("Разработчик", companyUser1.getId(), BigDecimal.valueOf(100000), university);
        assertEquals(1, result4.size());
    }

    @Test
    @DisplayName("Test top vacancies by views")
    void testFindTopVacancies() {
        List<Vacancy> top = vacancyDAO.findTopVacanciesByViews();
        assertEquals(3, top.size());
        assertTrue(top.get(0).getNumberOfViews() >= top.get(1).getNumberOfViews());
    }

    @Test
    @DisplayName("Test view increment")
    void testIncrementViews() {
        Vacancy vacancy = vacancyDAO.getById(1L);
        Long initialViews = vacancy.getNumberOfViews();
        vacancyDAO.incrementViews(1L);
        assertEquals(initialViews + 1, vacancyDAO.getById(1L).getNumberOfViews());
    }

    @BeforeEach
    void setUp() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Инициализация ролей и статусов
            employerRole = new Role("Работодатель");
            roleDAO.save(employerRole);

            activeStatus = new UserStatus("Активен");
            userStatusDAO.save(activeStatus);

            // Учебные заведения
            university = new EducationalInstitution("Университет");
            EducationalInstitution college = new EducationalInstitution("Колледж");
            educationalInstitutionDAO.save(university);
            educationalInstitutionDAO.save(college);

            // Компании (как пользователи)
            companyUser1 = new SiteUser(
                    "company1", "pass1", employerRole, activeStatus,
                    "Tech Corp", "tech@corp.com", "Москва", null
            );

            companyUser2 = new SiteUser(
                    "company2", "pass2", employerRole, activeStatus,
                    "Dev Inc", "dev@inc.com", "СПб", null
            );
            siteUserDAO.save(companyUser1);
            siteUserDAO.save(companyUser2);

            // Создание вакансий
            Vacancy v1 = new Vacancy(
                    "Java Разработчик",
                    "Разработка ПО",
                    BigDecimal.valueOf(120000),
                    companyUser1,
                    university,
                    100
            );

            Vacancy v2 = new Vacancy(
                    "Senior Python Разработчик",
                    "Бэкенд разработка",
                    BigDecimal.valueOf(160000),
                    companyUser1,
                    college,
                    150
            );

            Vacancy v3 = new Vacancy(
                    "Менеджер проектов",
                    "Управление проектами",
                    BigDecimal.valueOf(90000),
                    companyUser2,
                    null,
                    80
            );

            vacancyDAO.save(v1);
            vacancyDAO.save(v2);
            vacancyDAO.save(v3);

            session.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE vacancy RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE site_user RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE roles RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE user_statuses RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE educational_institutions RESTART IDENTITY CASCADE").executeUpdate();
            session.getTransaction().commit();
        }
    }
}