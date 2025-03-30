package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.SelectedResumeDAO;
import ru.msu.cmc.webprak.models.SelectedResume;

import java.util.List;

@Repository
public class SelectedResumeDAOImpl extends CommonDAOImpl<SelectedResume, Long> implements SelectedResumeDAO {

    public SelectedResumeDAOImpl() {
        super(SelectedResume.class);
    }

    @Override
    public List<SelectedResume> findByCompanyId(Long companyId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SelectedResume> query = builder.createQuery(SelectedResume.class);
            Root<SelectedResume> root = query.from(SelectedResume.class);

            Predicate condition = builder.equal(root.get("company").get("id"), companyId);
            query.select(root).where(condition);

            return session.createQuery(query).getResultList();
        }
    }
}
