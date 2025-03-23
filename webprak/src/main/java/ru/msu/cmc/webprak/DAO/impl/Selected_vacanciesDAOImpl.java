package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.Selected_vacanciesDAO;
import ru.msu.cmc.webprak.models.Selected_vacancies;

import java.util.List;

@Repository
public class Selected_vacanciesDAOImpl extends CommonDAOImpl<Selected_vacancies, Long> implements Selected_vacanciesDAO {
    public Selected_vacanciesDAOImpl() {
        super(Selected_vacancies.class);
    }

    @Override
    public List<Selected_vacancies> findByCompanyId(Long applicantId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Selected_vacancies> query = session.createQuery(
                    "FROM Selected_vacancies WHERE applicant_id.id = :applicantId", Selected_vacancies.class);
            query.setParameter("applicantId", applicantId);
            return query.getResultList();
        }
    }

}
