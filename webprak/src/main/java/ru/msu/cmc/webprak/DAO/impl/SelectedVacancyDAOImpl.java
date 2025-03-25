package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.SelectedVacancyDAO;
import ru.msu.cmc.webprak.models.SelectedVacancy;

import java.util.List;

@Repository
public class SelectedVacancyDAOImpl extends CommonDAOImpl<SelectedVacancy, Long> implements SelectedVacancyDAO {

    public SelectedVacancyDAOImpl() {
        super(SelectedVacancy.class);
    }  @Override
    public List<SelectedVacancy> findByApplicantId(Long applicantId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SelectedVacancy> query = builder.createQuery(SelectedVacancy.class);
            Root<SelectedVacancy> root = query.from(SelectedVacancy.class);

            Predicate condition = builder.equal(root.get("applicant").get("id"), applicantId);
            query.select(root).where(condition);

            return session.createQuery(query).getResultList();
        }
    }
}
