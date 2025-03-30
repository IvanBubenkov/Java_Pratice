package ru.msu.cmc.webprak.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class ResumeDAOImplTest {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserStatusDAO userStatusDAO;

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;

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
        // Тест 1: Поиск по названию резюме
        List<Resume> result1 = resumeDAO.findByCriteria("Программист", null, null);
        assertFalse(result1.isEmpty(), "Ожидается, что резюме найдётся");
        assertEquals(1, result1.size(), "Должно быть найдено только одно резюме");
        assertEquals("Программист", result1.get(0).getResumeName());

        // Тест 2: Поиск по userId (6L)
        List<Resume> result2 = resumeDAO.findByCriteria(null, 6L, null);
        assertFalse(result2.isEmpty(), "Ожидается, что резюме найдутся");
        assertEquals(2, result2.size(), "Должно быть найдено два резюме");
        assertTrue(result2.stream().anyMatch(r -> r.getResumeName().equals("Инженер-строитель")));
        assertTrue(result2.stream().anyMatch(r -> r.getResumeName().equals("Экономист")));

        // Тест 3: Поиск по минимальной зарплате (55000)
        List<Resume> result3 = resumeDAO.findByCriteria(null, null, new BigDecimal("55000"));
        assertFalse(result3.isEmpty(), "Ожидается, что резюме найдутся");
        assertEquals(5, result3.size(), "Должно быть найдено 5 резюме с зарплатой >= 55000");

        // Тест 4: Поиск по всем параметрам (userId = 7L, зарплата >= 50000, название "Логист")
        List<Resume> result4 = resumeDAO.findByCriteria("Логист", 7L, new BigDecimal("50000"));
        assertFalse(result4.isEmpty(), "Ожидается, что резюме найдётся");
        assertEquals(1, result4.size(), "Должно быть найдено только одно резюме");
        assertEquals("Логист", result4.get(0).getResumeName());

        // Тест 5: Поиск с несуществующими параметрами (ожидаем пустой список)
        List<Resume> result5 = resumeDAO.findByCriteria("Неизвестное резюме", null, null);
        assertTrue(result5.isEmpty(), "Ожидается, что не найдётся ни одного резюме");
    }

    @Test
    @DisplayName("Test findTopResumesByViews() method")
    void testFindTopResumesByViews() {
        List<Resume> topResumes = resumeDAO.findTopResumesByViews();

        // Проверяем, что список не пустой
        assertFalse(topResumes.isEmpty(), "Ожидается, что будут найдены резюме");

        // Проверяем, что результаты отсортированы по убыванию количества просмотров
        for (int i = 1; i < topResumes.size(); i++) {
            assertTrue(
                    topResumes.get(i - 1).getNumberOfViews() >= topResumes.get(i).getNumberOfViews(),
                    "Резюме должны быть отсортированы по убыванию просмотров"
            );
        }

        // Проверяем, что первое резюме действительно имеет максимальное количество просмотров
        Resume topResume = topResumes.get(0);
        assertEquals("Руководитель проекта", topResume.getResumeName(), "Ожидается, что у 'Руководителя проекта' будет больше всего просмотров");
        assertEquals(90L, topResume.getNumberOfViews(), "Ожидается, что 'Руководитель проекта' имеет 90 просмотров");
    }

    @BeforeEach
    void beforeEach() {

        Role applicant = new Role("Соискатель");
        roleDAO.save(applicant);

        UserStatus looking_for_a_job = new UserStatus(1L, "Ищет работу");
        userStatusDAO.save(looking_for_a_job);

        EducationalInstitution ed_in_1 = new EducationalInstitution(1L,"Среднее общее образование");
        educationalInstitutionDAO.save(ed_in_1);
        EducationalInstitution ed_in_2 = new EducationalInstitution(2L,"Среднее профессиональное образование");
        educationalInstitutionDAO.save(ed_in_2);
        EducationalInstitution ed_in_3 = new EducationalInstitution(3L,"Бакалавриат");
        educationalInstitutionDAO.save(ed_in_3);
        EducationalInstitution ed_in_4 = new EducationalInstitution(4L,"Специалитет");
        educationalInstitutionDAO.save(ed_in_4);
        EducationalInstitution ed_in_5 = new EducationalInstitution(5L, "Магистратура");
        educationalInstitutionDAO.save(ed_in_5);

        SiteUser user5 = new SiteUser(5L, "vasiliev", "pass5", applicant, looking_for_a_job, "Васильев Иван Петрович", "vasiliev@mail.ru", "Москва, ул. Пушкина, д. 8", ed_in_3);
        siteUserDAO.save(user5);
        SiteUser user6 = new SiteUser(6L, "alexeeva", "pass6", roleDAO.getRoleByName("Соискатель"), looking_for_a_job, "Алексеева Мария Сергеевна", "alexeeva@icloud.com", "Санкт-Петербург, ул. Достоевского, д. 12", ed_in_4);
        siteUserDAO.save(user6);
        SiteUser user7 = new SiteUser(7L, "pavlov", "pass7", roleDAO.getRoleByName("Соискатель"), looking_for_a_job, "Павлов Дмитрий Алексеевич", "pavlov@gmail.com", "Новосибирск, ул. Советская, д. 7", ed_in_2);
        siteUserDAO.save(user7);
        SiteUser user8 = new SiteUser(8L, "nikiforov", "pass8", roleDAO.getRoleByName("Соискатель"), looking_for_a_job, "Никифоров Алексей Викторович", "nikiforov@ya.ru", "Ростов-на-Дону, ул. Садовая, д. 14", ed_in_5);
        siteUserDAO.save(user8);
        SiteUser user9 = new SiteUser(9L, "kuznetsova", "pass9", roleDAO.getRoleByName("Соискатель"), looking_for_a_job, "Кузнецова Елена Андреевна", "kuznetsova@mail.ru", "Воронеж, ул. Ломоносова, д. 3", ed_in_3);
        siteUserDAO.save(user9);

        List<Resume> resumes = new ArrayList<>();
        resumes.add(new Resume(1L, user5, new BigDecimal(60000), 50L, "Программист"));
        resumes.add(new Resume(2L, user6, new BigDecimal(55000), 40L, "Инженер-строитель"));
        resumes.add(new Resume(3L, user7, new BigDecimal(50000), 60L, "Логист"));
        resumes.add(new Resume(4L, user8, new BigDecimal(48000), 30L, "Бухгалтер"));
        resumes.add(new Resume(5L, user9, new BigDecimal(53000), 70L, "Маркетолог"));
        resumes.add(new Resume(6L, user5, new BigDecimal(62000), 80L, "Сетевой инженер"));
        resumes.add(new Resume(7L, user6, new BigDecimal(58000), 55L, "Экономист"));
        resumes.add(new Resume(8L, user7, new BigDecimal(47000), 45L, "Менеджер по продажам"));
        resumes.add(new Resume(9L, user8, new BigDecimal(45000), 35L, "Системный администратор"));
        resumes.add(new Resume(10L, user9, new BigDecimal(70000), 90L, "Руководитель проекта"));

        resumeDAO.saveCollection(resumes);
    }

    @AfterEach
    void annihilation() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.createNativeQuery("TRUNCATE TABLE roles RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE user_statuses RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE educational_institutions RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE site_user RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE resume RESTART IDENTITY CASCADE").executeUpdate();

            session.createNativeQuery("ALTER SEQUENCE role_id_seq RESTART WITH 1").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE user_statuse_id_seq RESTART WITH 1").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE educational_institution_id_seq RESTART WITH 1").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE site_user_id_seq RESTART WITH 1").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE resume_id_seq RESTART WITH 1").executeUpdate();

            session.getTransaction().commit();
        }
    }
}