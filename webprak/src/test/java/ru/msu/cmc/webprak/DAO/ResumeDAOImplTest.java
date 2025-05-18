/*package ru.msu.cmc.webprak.DAO;

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

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class ResumeDAOImplTest {

    @Autowired
    private ResumeDAO resumeDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @DisplayName("Test getAll() method")
    void testGetAll() {
        List<Resume> resumes = (List<Resume>) resumeDAO.getAll();
        assertNotNull(resumes);
        assertFalse(resumes.isEmpty());
    }

    @Test
    @DisplayName("Test findByCriteria() method")
    void testFindByCriteria() {
        SiteUser user6 = siteUserDAO.getUserByLogin("alexeeva");
        SiteUser user7 = siteUserDAO.getUserByLogin("pavlov");

        List<Resume> result1 = resumeDAO.findByCriteria("Программист", null, null, null, null);
        assertFalse(result1.isEmpty());
        assertEquals(1, result1.size());
        assertEquals("Программист", result1.get(0).getResumeName());

        List<Resume> result2 = resumeDAO.findByCriteria(null, user6.getId(), null, null, null);
        assertFalse(result2.isEmpty());
        assertEquals(2, result2.size());
        assertTrue(result2.stream().anyMatch(r -> r.getResumeName().equals("Инженер-строитель")));
        assertTrue(result2.stream().anyMatch(r -> r.getResumeName().equals("Экономист")));

        List<Resume> result3 = resumeDAO.findByCriteria(null, null, new BigDecimal("55000"));
        assertFalse(result3.isEmpty());
        assertEquals(5, result3.size());

        List<Resume> result4 = resumeDAO.findByCriteria("Логист", user7.getId(), new BigDecimal("50000"));
        assertFalse(result4.isEmpty());
        assertEquals(1, result4.size());
        assertEquals("Логист", result4.get(0).getResumeName());

        List<Resume> result5 = resumeDAO.findByCriteria("Неизвестное резюме", null, null);
        assertTrue(result5.isEmpty());
        List<Resume> result6 = resumeDAO.findByCriteria(null, null, null);
        assertEquals(10, result6.size());
    }

    @Test
    @DisplayName("Test findTopResumesByViews() method")
    void testFindTopResumesByViews() {
        List<Resume> topResumes = resumeDAO.findTopResumesByViews();

        assertFalse(topResumes.isEmpty());
        assertEquals(10, topResumes.size());

        for (int i = 1; i < topResumes.size(); i++) {
            assertTrue(topResumes.get(i-1).getNumberOfViews() >= topResumes.get(i).getNumberOfViews());
        }

        Resume first = topResumes.get(0);
        assertEquals("Руководитель проекта", first.getResumeName());
        assertEquals(90L, first.getNumberOfViews());
    }

    @Test
    @DisplayName("Test deleteById() method")
    void testDeleteById() {
        SiteUser user = siteUserDAO.getUserByLogin("vasiliev");
        assertNotNull(user, "Пользователь 'vasiliev' должен существовать");

        Resume testResume = new Resume(
                user,
                new BigDecimal("100000"),
                200L,
                "Тестовое резюме для удаления"
        );
        resumeDAO.save(testResume);
        Long resumeId = testResume.getId();
        assertNotNull(resumeId, "ID резюме не должно быть null");

        Resume savedResume = resumeDAO.getById(resumeId);
        assertNotNull(savedResume, "Резюме должно существовать в базе до удаления");

        resumeDAO.deleteById(resumeId);

        Resume deletedResume = resumeDAO.getById(resumeId);
        assertNull(deletedResume, "Резюме должно быть удалено из базы");

        List<Resume> result = resumeDAO.findByCriteria("Тестовое резюме для удаления", null, null);
        assertTrue(result.isEmpty(), "Удаленное резюме не должно находиться в результатах поиска");
    }

    @Test
    @DisplayName("Test incrementViews() method")
    void testIncrementViews() {
        SiteUser user = siteUserDAO.getUserByLogin("vasiliev");
        assertNotNull(user, "Пользователь должен существовать");

        Resume testResume = new Resume(
                user,
                new BigDecimal("80000"),
                25L,
                "Тестовое резюме для проверки просмотров"
        );
        resumeDAO.save(testResume);
        Long resumeId = testResume.getId();

        resumeDAO.incrementViews(resumeId);
        Resume updatedResume = resumeDAO.getById(resumeId);
        assertEquals(26L, updatedResume.getNumberOfViews(),
                "Количество просмотров должно увеличиться на 1 после первого вызова");

        resumeDAO.incrementViews(resumeId);
        updatedResume = resumeDAO.getById(resumeId);
        assertEquals(27L, updatedResume.getNumberOfViews(),
                "Количество просмотров должно увеличиться на 1 после второго вызова");

        assertDoesNotThrow(() -> resumeDAO.incrementViews(9999L),
                "Метод не должен выбрасывать исключение для несуществующего ID");

        Resume otherResume = resumeDAO.getById(1L);
        assertNotNull(otherResume);
        assertEquals(50L, otherResume.getNumberOfViews(),
                "Количество просмотров других резюме не должно меняться");
    }

    @BeforeEach
    void beforeEach() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Role applicant = new Role("Соискатель");
            UserStatus status = new UserStatus("Ищет работу");
            session.persist(applicant);
            session.persist(status);

            List<EducationalInstitution> institutions = List.of(
                    new EducationalInstitution("Среднее общее образование"),
                    new EducationalInstitution("Среднее профессиональное образование"),
                    new EducationalInstitution("Бакалавриат"),
                    new EducationalInstitution("Специалитет"),
                    new EducationalInstitution("Магистратура")
            );
            institutions.forEach(session::persist);

            List<SiteUser> users = List.of(
                    new SiteUser("vasiliev", "pass5", applicant, status,
                            "Васильев Иван Петрович", "vasiliev@mail.ru",
                            "Москва, ул. Пушкина, д. 8", institutions.get(2)),
                    new SiteUser("alexeeva", "pass6", applicant, status,
                            "Алексеева Мария Сергеевна", "alexeeva@icloud.com",
                            "Санкт-Петербург, ул. Достоевского, д. 12", institutions.get(3)),
                    new SiteUser("pavlov", "pass7", applicant, status,
                            "Павлов Дмитрий Алексеевич", "pavlov@gmail.com",
                            "Новосибирск, ул. Советская, д. 7", institutions.get(1)),
                    new SiteUser("nikiforov", "pass8", applicant, status,
                            "Никифоров Алексей Викторович", "nikiforov@ya.ru",
                            "Ростов-на-Дону, ул. Садовая, д. 14", institutions.get(4)),
                    new SiteUser("kuznetsova", "pass9", applicant, status,
                            "Кузнецова Елена Андреевна", "kuznetsova@mail.ru",
                            "Воронеж, ул. Ломоносова, д. 3", institutions.get(2))
            );
            users.forEach(session::persist);

            List<Resume> resumes = List.of(
                    new Resume(users.get(0), new BigDecimal(60000), 50L, "Программист"),
                    new Resume(users.get(1), new BigDecimal(55000), 40L, "Инженер-строитель"),
                    new Resume(users.get(2), new BigDecimal(50000), 60L, "Логист"),
                    new Resume(users.get(3), new BigDecimal(48000), 30L, "Бухгалтер"),
                    new Resume(users.get(4), new BigDecimal(53000), 70L, "Маркетолог"),
                    new Resume(users.get(0), new BigDecimal(62000), 80L, "Сетевой инженер"),
                    new Resume(users.get(1), new BigDecimal(58000), 55L, "Экономист"),
                    new Resume(users.get(2), new BigDecimal(47000), 45L, "Менеджер по продажам"),
                    new Resume(users.get(3), new BigDecimal(45000), 35L, "Системный администратор"),
                    new Resume(users.get(4), new BigDecimal(70000), 90L, "Руководитель проекта")
            );
            resumes.forEach(session::persist);

            session.getTransaction().commit();
        }
    }

    @AfterEach
    void annihilation() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE roles, user_statuses, educational_institutions, site_user, resume RESTART IDENTITY CASCADE")
                    .executeUpdate();
            session.getTransaction().commit();
        }
    }
}

 */