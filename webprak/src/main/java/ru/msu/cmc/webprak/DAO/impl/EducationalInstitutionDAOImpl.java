package ru.msu.cmc.webprak.DAO.impl;

import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.EducationalInstitutionDAO;
import ru.msu.cmc.webprak.models.EducationalInstitution;

@Repository
public class EducationalInstitutionDAOImpl extends CommonDAOImpl<EducationalInstitution, Long> implements EducationalInstitutionDAO {
    public EducationalInstitutionDAOImpl() {
        super(EducationalInstitution.class);
    }
}
