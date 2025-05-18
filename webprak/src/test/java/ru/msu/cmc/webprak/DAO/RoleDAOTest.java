/*package ru.msu.cmc.webprak.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.msu.cmc.webprak.models.Role;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class RoleDAOTest {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @DisplayName("Test getRoleByName() method")
    void testGetRoleByName() {
        // 1. Проверка существующей роли
        Role role = roleDAO.getRoleByName("Соискатель");
        assertNotNull(role, "Роль 'Соискатель' должна существовать");
        assertEquals(1L, role.getId(), "ID роли должно соответствовать ожидаемому");

        // 2. Проверка несуществующей роли
        Role nonExistent = roleDAO.getRoleByName("Несуществующая роль");
        assertNull(nonExistent, "Должен возвращаться null для несуществующей роли");

        // 3. Проверка дубликатов (если roleName не уникален)
        Role dup1 = new Role("Дубликат");
        Role dup2 = new Role("Дубликат");
        roleDAO.save(dup1);
        roleDAO.save(dup2);

        Role result = roleDAO.getRoleByName("Дубликат");
        assertNull(result, "Должен возвращаться null при нескольких совпадениях");
    }

    @BeforeEach
    void beforeEach() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(new Role("Соискатель"));
            session.persist(new Role("Работодатель"));
            session.persist(new Role("Администратор"));
            session.getTransaction().commit();
        }
    }

    @AfterEach
    void annihilation() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE roles RESTART IDENTITY CASCADE").executeUpdate();
            session.getTransaction().commit();
        }
    }
}

 */