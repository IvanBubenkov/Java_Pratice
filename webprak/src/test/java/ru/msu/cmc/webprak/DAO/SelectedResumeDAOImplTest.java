package ru.msu.cmc.webprak.DAO;

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
public class SelectedResumeDAOImplTest {

    @Autowired
    private SelectedResumeDAO selectedResumeDAO;

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserStatusDAO userStatusDAO;

    @Autowired
    private EducationalInstitutionDAO educationalInstitutionDAO;

    @Autowired
    private ResumeDAO resumeDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private SiteUser company1;
    private SiteUser company2;
    private SiteUser applicant1;
    private Resume resume1;
    private Resume resume2;

    @Test
    @DisplayName("Test finding selected resumes by company ID")
    void testFindByCompanyId() {
        // Test for first company
        List<SelectedResume> company1Resumes = selectedResumeDAO.findByCompanyId(company1.getId());
        assertEquals(2, company1Resumes.size());

        // Test for second company
        List<SelectedResume> company2Resumes = selectedResumeDAO.findByCompanyId(company2.getId());
        assertEquals(1, company2Resumes.size());

        // Test for non-existing company
        List<SelectedResume> nonExistent = selectedResumeDAO.findByCompanyId(999L);
        assertTrue(nonExistent.isEmpty());
    }

    @BeforeEach
    void setUp() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Role employerRole = new Role();
            employerRole.setRoleName("Работодатель");

            Role applicantRole = new Role();
            applicantRole.setRoleName("Соискатель");

            UserStatus activeStatus = new UserStatus();
            activeStatus.setStatusName("Активен");

            roleDAO.save(employerRole);
            roleDAO.save(applicantRole);
            userStatusDAO.save(activeStatus);

            EducationalInstitution university = new EducationalInstitution();
            university.setEducationLevel("Университет");
            educationalInstitutionDAO.save(university);

            company1 = new SiteUser();
            company1.setLogin("company1");
            company1.setPassword("pass");
            company1.setRole(employerRole);
            company1.setStatus(activeStatus);
            company1.setFullNameCompany("Tech Corp");
            company1.setEmail("tech@corp.com");
            company1.setHomeAddress("Москва");

            company2 = new SiteUser();
            company2.setLogin("company2");
            company2.setPassword("pass");
            company2.setRole(employerRole);
            company2.setStatus(activeStatus);
            company2.setFullNameCompany("Dev Inc");
            company2.setEmail("dev@inc.com");
            company2.setHomeAddress("СПб");

            applicant1 = new SiteUser();
            applicant1.setLogin("applicant1");
            applicant1.setPassword("pass");
            applicant1.setRole(applicantRole);
            applicant1.setStatus(activeStatus);
            applicant1.setFullNameCompany("Иван Иванов");
            applicant1.setEmail("ivan@mail.ru");
            applicant1.setHomeAddress("Москва");
            applicant1.setEducation(university);

            siteUserDAO.save(company1);
            siteUserDAO.save(company2);
            siteUserDAO.save(applicant1);

            resume1 = new Resume();
            resume1.setUser(applicant1);
            resume1.setResumeName("Java Developer");

            resume2 = new Resume();
            resume2.setUser(applicant1);
            resume2.setResumeName("Team Lead");

            resumeDAO.save(resume1);
            resumeDAO.save(resume2);

            SelectedResume sr1 = new SelectedResume();
            sr1.setResume(resume1);
            sr1.setCompany(company1);

            SelectedResume sr2 = new SelectedResume();
            sr2.setResume(resume2);
            sr2.setCompany(company1);

            SelectedResume sr3 = new SelectedResume();
            sr3.setResume(resume1);
            sr3.setCompany(company2);

            selectedResumeDAO.save(sr1);
            selectedResumeDAO.save(sr2);
            selectedResumeDAO.save(sr3);

            session.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE selected_resumes RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE resume RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE site_user RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE roles RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE user_statuses RESTART IDENTITY CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE educational_institutions RESTART IDENTITY CASCADE").executeUpdate();
            session.getTransaction().commit();
        }
    }
}