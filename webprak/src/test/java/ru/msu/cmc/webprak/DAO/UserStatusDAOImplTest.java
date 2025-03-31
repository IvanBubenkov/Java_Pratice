package ru.msu.cmc.webprak.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.UserStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class UserStatusDAOImplTest {

    @Autowired
    private UserStatusDAO userStatusDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private UserStatus activeStatus;
    private UserStatus bannedStatus;

    @Test
    @DisplayName("Test status retrieval by name")
    void testGetUserStatusByName() {
        // Test existing status
        UserStatus result = userStatusDAO.getUserStatusByName("Активен");
        assertNotNull(result);
        assertEquals(activeStatus.getId(), result.getId());

        // Test non-existing status
        assertNull(userStatusDAO.getUserStatusByName("Несуществующий"));
    }

    @BeforeEach
    void setUp() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            activeStatus = new UserStatus();
            activeStatus.setStatusName("Активен");

            bannedStatus = new UserStatus();
            bannedStatus.setStatusName("Заблокирован");

            userStatusDAO.save(activeStatus);
            userStatusDAO.save(bannedStatus);

            session.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE user_statuses RESTART IDENTITY CASCADE").executeUpdate();
            session.getTransaction().commit();
        }
    }
}