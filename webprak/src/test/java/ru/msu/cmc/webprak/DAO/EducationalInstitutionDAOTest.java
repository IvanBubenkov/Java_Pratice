package ru.msu.cmc.webprak.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.EducationalInstitution;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class EducationalInstitutionDAOTest {

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @DisplayName("Test getAll() method")
    void testGetAll() {
        List<EducationalInstitution> institutions = (List<EducationalInstitution>) educationalInstitutionDAO.getAll();
        assertNotNull(institutions);
        assertFalse(institutions.isEmpty());
    }

    @Test
    @DisplayName("Test getById() method")
    void testGetById() {
        EducationalInstitution institution = educationalInstitutionDAO.getById(1L);
        assertNotNull(institution);
        assertEquals("Среднее общее образование", institution.getEducationLevel());
    }

    @Test
    @DisplayName("Test save() method")
    void testSave() {
        EducationalInstitution newInstitution = new EducationalInstitution("Экспериментальное образование");
        educationalInstitutionDAO.save(newInstitution);

        assertNotNull(newInstitution.getId());
        EducationalInstitution retrieved = educationalInstitutionDAO.getById(newInstitution.getId());
        assertEquals("Экспериментальное образование", retrieved.getEducationLevel());
    }

    @Test
    @DisplayName("Test update() method")
    void testUpdate() {
        EducationalInstitution institution = educationalInstitutionDAO.getById(1L);
        assertNotNull(institution);

        institution.setEducationLevel("Обновлённое образование");
        educationalInstitutionDAO.update(institution);

        EducationalInstitution updated = educationalInstitutionDAO.getById(1L);
        assertEquals("Обновлённое образование", updated.getEducationLevel());
    }

    @Test
    @DisplayName("Test delete() method")
    void testDelete() {
        EducationalInstitution institution = new EducationalInstitution("Временное образование");
        educationalInstitutionDAO.save(institution);
        Long id = institution.getId();

        educationalInstitutionDAO.delete(institution);
        assertNull(educationalInstitutionDAO.getById(id));
    }

    @BeforeEach
    void beforeEach() {
        List<EducationalInstitution> institutions = new ArrayList<>();
        institutions.add(new EducationalInstitution("Среднее общее образование"));
        institutions.add(new EducationalInstitution("Среднее профессиональное образование"));
        institutions.add(new EducationalInstitution("Бакалавриат"));
        institutions.add(new EducationalInstitution("Специалитет"));
        institutions.add(new EducationalInstitution("Магистратура"));
        institutions.add(new EducationalInstitution("Аспирантура"));
        institutions.add(new EducationalInstitution("Докторантура"));
        institutions.add(new EducationalInstitution("Повышение квалификации"));
        institutions.add(new EducationalInstitution("Переподготовка"));
        institutions.add(new EducationalInstitution("Колледж"));
        educationalInstitutionDAO.saveCollection(institutions);
    }

    @AfterEach
    void annihilation() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE educational_institutions RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE educational_institutions_education_id_seq RESTART WITH 1").executeUpdate();
            session.getTransaction().commit();
        }
    }
}
