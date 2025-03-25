package ru.msu.cmc.webprak.DAO.impl;

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
            return session.createQuery(
                            "FROM Selected_resumes sr WHERE sr.company_id.id = :companyId", SelectedResume.class)
                    .setParameter("companyId", companyId)
                    .getResultList();

        }
    }
}
