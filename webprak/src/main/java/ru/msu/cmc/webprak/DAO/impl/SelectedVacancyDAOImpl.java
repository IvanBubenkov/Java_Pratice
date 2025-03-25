package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.SelectedVacancyDAO;
import ru.msu.cmc.webprak.models.SelectedVacancy;

import java.util.List;

@Repository
public class SelectedVacancyDAOImpl extends CommonDAOImpl<SelectedVacancy, Long> implements SelectedVacancyDAO {
    public SelectedVacancyDAOImpl() {
        super(SelectedVacancy.class);
    }

    @Override
    public List<SelectedVacancy> findByCompanyId(Long applicantId) {
        try (Session session = sessionFactory.openSession()) {
            Query<SelectedVacancy> query = session.createQuery(
                    "FROM Selected_vacancies WHERE applicant_id.id = :applicantId", SelectedVacancy.class);
            query.setParameter("applicantId", applicantId);
            return query.getResultList();
        }
    }

}
