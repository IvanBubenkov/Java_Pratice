package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.models.EducationalInstitution;
import ru.msu.cmc.webprak.models.Role;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.UserStatus;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SiteUserDAOImpl extends CommonDAOImpl<SiteUser, Long> implements SiteUserDAO {

    public SiteUserDAOImpl() {
        super(SiteUser.class);
    }

    @Override
    public SiteUser findByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SiteUser> query = builder.createQuery(SiteUser.class);
            Root<SiteUser> root = query.from(SiteUser.class);

            query.select(root).where(builder.equal(root.get("login"), login));

            return session.createQuery(query).uniqueResult();
        }
    }

    @Override
    public List<SiteUser> findByCriteria(Role role, UserStatus status, EducationalInstitution education) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SiteUser> query = builder.createQuery(SiteUser.class);
            Root<SiteUser> root = query.from(SiteUser.class);

            List<Predicate> predicates = new ArrayList<>();

            if (role != null) {
                predicates.add(builder.equal(root.get("role"), role));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (education != null) {
                predicates.add(builder.equal(root.get("education"), education));
            }

            query.select(root).where(predicates.toArray(new Predicate[0]));

            return session.createQuery(query).getResultList();
        }
    }

    @Override
    public SiteUser getUserByLogin(String login) {
        System.out.println("=== DEBUG: поиск пользователя ===");
        System.out.println("Ищем логин: '" + login + "'");

        try (Session session = sessionFactory.openSession()) {
            System.out.println("Сессия открыта успешно");

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SiteUser> query = builder.createQuery(SiteUser.class);
            Root<SiteUser> root = query.from(SiteUser.class);

            query.select(root).where(builder.equal(root.get("login"), login));

            System.out.println("Сформирован запрос: " + query.toString());

            SiteUser result = session.createQuery(query).uniqueResult();
            System.out.println("Найден пользователь: " + (result != null ? result.getLogin() : "null"));

            return result;
        } catch (Exception e) {
            System.err.println("Ошибка при поиске пользователя: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

