package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.models.EducationalInstitution;
import ru.msu.cmc.webprak.models.Role;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.UserStatus;

import java.util.List;

@Repository
public class SiteUserDAOImpl extends CommonDAOImpl<SiteUser, Long> implements SiteUserDAO {

    public SiteUserDAOImpl() {
        super(SiteUser.class);
    }

    @Override
    public SiteUser findByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            Query<SiteUser> query = session.createQuery(
                    "FROM Site_user WHERE login = :login", SiteUser.class);
            query.setParameter("login", login);
            return query.uniqueResult();  // Возвращаем единственный результат
        }
    }

    @Override
    public List<SiteUser> findByRole(Role role) {
        try (Session session = sessionFactory.openSession()) {
            Query<SiteUser> query = session.createQuery(
                    "FROM Site_user WHERE role = :role", SiteUser.class);
            query.setParameter("role", role);
            return query.getResultList();  // Возвращаем список пользователей с указанной ролью
        }
    }

    @Override
    public List<SiteUser> findByStatus(UserStatus status) {
        try (Session session = sessionFactory.openSession()) {
            Query<SiteUser> query = session.createQuery(
                    "FROM Site_user WHERE status = :status", SiteUser.class);
            query.setParameter("status", status);
            return query.getResultList();  // Возвращаем список пользователей с указанным статусом
        }
    }

    @Override
    public List<SiteUser> findByEducation(EducationalInstitution education) {
        try (Session session = sessionFactory.openSession()) {
            Query<SiteUser> query = session.createQuery(
                    "FROM Site_user WHERE education = :education", SiteUser.class);
            query.setParameter("education", education);
            return query.getResultList();  // Возвращаем список пользователей с указанным образовательным учреждением
        }
    }
}

