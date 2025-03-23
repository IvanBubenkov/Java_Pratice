package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.Selected_resumesDAO;
import ru.msu.cmc.webprak.models.Selected_resumes;

import java.util.List;

@Repository
public class Selected_resumesDAOImpl extends CommonDAOImpl<Selected_resumes, Long> implements Selected_resumesDAO {

    public Selected_resumesDAOImpl() {
        super(Selected_resumes.class);
    }

    @Override
    public List<Selected_resumes> findByCompanyId(Long companyId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM Selected_resumes sr WHERE sr.company_id.id = :companyId", Selected_resumes.class)
                    .setParameter("companyId", companyId)
                    .getResultList();

        }
    }
}
