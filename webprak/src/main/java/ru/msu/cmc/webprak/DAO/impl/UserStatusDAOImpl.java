package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.UserStatusDAO;
import ru.msu.cmc.webprak.models.UserStatus;

import java.util.List;

@Repository
public class UserStatusDAOImpl extends CommonDAOImpl<UserStatus, Long> implements UserStatusDAO {
    public UserStatusDAOImpl() {
        super(UserStatus.class);
    }
    @Override
    public UserStatus getUserStatusByName(String statusName) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UserStatus> query = builder.createQuery(UserStatus.class);
            Root<UserStatus> root = query.from(UserStatus.class);

            Predicate condition = builder.equal(root.get("statusName"), statusName);
            query.select(root).where(condition);

            Query<UserStatus> resultQuery = session.createQuery(query);
            List<UserStatus> statuses = resultQuery.getResultList();
            
            return statuses.size() == 1 ? statuses.get(0) : null;
        }
    }
}
