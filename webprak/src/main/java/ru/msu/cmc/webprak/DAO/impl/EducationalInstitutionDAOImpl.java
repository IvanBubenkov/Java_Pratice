package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.EducationalInstitutionDAO;
import ru.msu.cmc.webprak.models.EducationalInstitution;

import java.util.List;

@Repository
public class EducationalInstitutionDAOImpl extends CommonDAOImpl<EducationalInstitution, Long> implements EducationalInstitutionDAO {
    public EducationalInstitutionDAOImpl() {
        super(EducationalInstitution.class);
    }

    @Override
    public EducationalInstitution getEducationalInstitutionByName(String educationLevel) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<EducationalInstitution> query = builder.createQuery(EducationalInstitution.class);
            Root<EducationalInstitution> root = query.from(EducationalInstitution.class);

            Predicate condition = builder.equal(root.get("educationLevel"), educationLevel);
            query.select(root).where(condition);

            Query<EducationalInstitution> resultQuery = session.createQuery(query);
            List<EducationalInstitution> institutions = resultQuery.getResultList();

            return institutions.size() == 1 ? institutions.get(0) : null;
        }
    }
}
