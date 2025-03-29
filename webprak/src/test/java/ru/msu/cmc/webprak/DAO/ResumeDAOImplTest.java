package ru.msu.cmc.webprak.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.DAO.impl.ResumeDAOImpl;
import ru.msu.cmc.webprak.models.Resume;
import ru.msu.cmc.webprak.models.SiteUser;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class ResumeDAOImplTest {

    @Autowired
    private ResumeDAO resumeDAO;

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
        assertEquals(6, result3.size(), "Должно быть найдено 6 резюме с зарплатой >= 55000");

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
        List<Resume> resumes = new ArrayList<>();

        SiteUser user1 = new SiteUser();
        user1.setId(5L);
        resumes.add(new Resume(1L, user1, new BigDecimal(60000), 50L, "Программист"));

        SiteUser user2 = new SiteUser();
        user2.setId(6L);
        resumes.add(new Resume(2L, user2, new BigDecimal(55000), 40L, "Инженер-строитель"));

        SiteUser user3 = new SiteUser();
        user3.setId(7L);
        resumes.add(new Resume(3L, user3, new BigDecimal(50000), 60L, "Логист"));

        SiteUser user4 = new SiteUser();
        user4.setId(8L);
        resumes.add(new Resume(4L, user4, new BigDecimal(48000), 30L, "Бухгалтер"));

        SiteUser user5 = new SiteUser();
        user5.setId(9L);
        resumes.add(new Resume(5L, user5, new BigDecimal(53000), 70L, "Маркетолог"));

        SiteUser user6 = new SiteUser();
        user6.setId(5L);
        resumes.add(new Resume(6L, user6, new BigDecimal(62000), 80L, "Сетевой инженер"));

        SiteUser user7 = new SiteUser();
        user7.setId(6L);
        resumes.add(new Resume(7L, user7, new BigDecimal(58000), 55L, "Экономист"));

        SiteUser user8 = new SiteUser();
        user8.setId(7L);
        resumes.add(new Resume(8L, user8, new BigDecimal(47000), 45L, "Менеджер по продажам"));

        SiteUser user9 = new SiteUser();
        user9.setId(8L);
        resumes.add(new Resume(9L, user9, new BigDecimal(45000), 35L, "Системный администратор"));

        SiteUser user10 = new SiteUser();
        user10.setId(9L);
        resumes.add(new Resume(10L, user10, new BigDecimal(70000), 90L, "Руководитель проекта"));

        resumeDAO.saveCollection(resumes);
    }

    @AfterEach
    void annihilation() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE resume RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE resume_id_seq RESTART WITH 1").executeUpdate();
            session.getTransaction().commit();
        }
    }
}