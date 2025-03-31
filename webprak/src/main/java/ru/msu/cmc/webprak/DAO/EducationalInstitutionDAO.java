package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.EducationalInstitution;

public interface EducationalInstitutionDAO extends CommonDAO<EducationalInstitution, Long> {
    EducationalInstitution getEducationalInstitutionByName(String educationLevel);
}
