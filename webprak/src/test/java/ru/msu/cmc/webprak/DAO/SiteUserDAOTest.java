/*package ru.msu.cmc.webprak.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class SiteUserDAOTest {

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

    private Role applicantRole;
    private Role employerRole;
    private UserStatus activeStatus;
    private UserStatus bannedStatus;
    private EducationalInstitution university;
    private EducationalInstitution college;

    @Test
    @DisplayName("Test findByLogin() method")
    void testFindByLogin() {
        SiteUser user = siteUserDAO.findByLogin("user1");
        assertNotNull(user, "Пользователь с логином 'user1' должен существовать");
        assertEquals("Иван Иванов", user.getFullNameCompany());
    }

    @Test
    @DisplayName("Test getUserByLogin() method")
    void testGetUserByLogin() {
        // 1. Существующий пользователь
        SiteUser user = siteUserDAO.getUserByLogin("user2");
        assertNotNull(user);
        assertEquals("Петр Петров", user.getFullNameCompany());

        // 2. Несуществующий пользователь
        SiteUser nonExistent = siteUserDAO.getUserByLogin("unknown");
        assertNull(nonExistent);
    }

    @Test
    @DisplayName("Test findByCriteria() method")
    void testFindByCriteria() {
        // 1. Поиск по роли
        List<SiteUser> result1 = siteUserDAO.findByCriteria(employerRole, null, null);
        assertEquals(1, result1.size());
        assertEquals("ООО Рога и Копыта", result1.get(0).getFullNameCompany());

        // 2. Поиск по статусу
        List<SiteUser> result2 = siteUserDAO.findByCriteria(null, bannedStatus, null);
        assertEquals(1, result2.size());
        assertEquals("user3", result2.get(0).getLogin());

        // 3. Комбинированный поиск
        List<SiteUser> result3 = siteUserDAO.findByCriteria(applicantRole, activeStatus, university);
        assertEquals(1, result3.size());
    }

    @Test
    @DisplayName("Test unique login constraint")
    void testUniqueLogin() {
        SiteUser duplicate = new SiteUser(
                "user1",
                "pass123",
                applicantRole,
                activeStatus,
                "Дубликат",
                "duplicate@mail.ru",
                "Адрес",
                college
        );
        assertThrows(Exception.class, () -> siteUserDAO.save(duplicate));
    }

    @BeforeEach
    void beforeEach() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Создание ролей
            applicantRole = new Role("Соискатель");
            employerRole = new Role("Работодатель");
            roleDAO.save(applicantRole);
            roleDAO.save(employerRole);

            // Создание статусов
            activeStatus = new UserStatus("Активен");
            bannedStatus = new UserStatus("Заблокирован");
            userStatusDAO.save(activeStatus);
            userStatusDAO.save(bannedStatus);

            // Создание учебных заведений
            university = new EducationalInstitution("Университет");
            college = new EducationalInstitution("Колледж");
            educationalInstitutionDAO.save(university);
            educationalInstitutionDAO.save(college);

            // Создание пользователей
            SiteUser user1 = new SiteUser(
                    "user1", "pass1", applicantRole, activeStatus,
                    "Иван Иванов", "ivan@mail.ru", "Москва", university
            );

            SiteUser user2 = new SiteUser(
                    "user2", "pass2", applicantRole, activeStatus,
                    "Петр Петров", "peter@mail.ru", "СПб", college
            );

            SiteUser user3 = new SiteUser(
                    "user3", "pass3", employerRole, bannedStatus,
                    "ООО Рога и Копыта", "company@mail.ru", "Офис", null
            );

            siteUserDAO.save(user1);
            siteUserDAO.save(user2);
            siteUserDAO.save(user3);

            session.getTransaction().commit();
        }
    }

    @AfterEach
    void annihilation() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE site_user RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE roles RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE user_statuses RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE educational_institutions RESTART IDENTITY CASCADE").executeUpdate();
            session.getTransaction().commit();
        }
    }
}

 */