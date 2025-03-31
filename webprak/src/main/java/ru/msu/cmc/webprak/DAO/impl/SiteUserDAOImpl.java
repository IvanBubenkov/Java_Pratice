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
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SiteUser> query = builder.createQuery(SiteUser.class);
            Root<SiteUser> root = query.from(SiteUser.class);

            // Условие поиска по логину
            Predicate condition = builder.equal(root.get("login"), login);
            query.select(root).where(condition);

            Query<SiteUser> q = session.createQuery(query);
            List<SiteUser> result = q.getResultList();

            // Возвращаем первого пользователя или null
            return result.isEmpty() ? null : result.get(0);
        }
    }
}

