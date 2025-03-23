package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.Site_userDAO;
import ru.msu.cmc.webprak.models.Educational_institutions;
import ru.msu.cmc.webprak.models.Roles;
import ru.msu.cmc.webprak.models.Site_user;
import ru.msu.cmc.webprak.models.User_statuses;

import java.util.List;

@Repository
public class Site_userDAOImpl extends CommonDAOImpl<Site_user, Long> implements Site_userDAO {

    public Site_userDAOImpl() {
        super(Site_user.class);
    }

    @Override
    public Site_user findByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            Query<Site_user> query = session.createQuery(
                    "FROM Site_user WHERE login = :login", Site_user.class);
            query.setParameter("login", login);
            return query.uniqueResult();  // Возвращаем единственный результат
        }
    }

    @Override
    public List<Site_user> findByRole(Roles role) {
        try (Session session = sessionFactory.openSession()) {
            Query<Site_user> query = session.createQuery(
                    "FROM Site_user WHERE role = :role", Site_user.class);
            query.setParameter("role", role);
            return query.getResultList();  // Возвращаем список пользователей с указанной ролью
        }
    }

    @Override
    public List<Site_user> findByStatus(User_statuses status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Site_user> query = session.createQuery(
                    "FROM Site_user WHERE status = :status", Site_user.class);
            query.setParameter("status", status);
            return query.getResultList();  // Возвращаем список пользователей с указанным статусом
        }
    }

    @Override
    public List<Site_user> findByEducation(Educational_institutions education) {
        try (Session session = sessionFactory.openSession()) {
            Query<Site_user> query = session.createQuery(
                    "FROM Site_user WHERE education = :education", Site_user.class);
            query.setParameter("education", education);
            return query.getResultList();  // Возвращаем список пользователей с указанным образовательным учреждением
        }
    }
}

