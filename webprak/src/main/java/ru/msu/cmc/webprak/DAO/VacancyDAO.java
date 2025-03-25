package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.EducationalInstitution;
import ru.msu.cmc.webprak.models.Vacancy;
import java.util.List;
import java.math.BigDecimal;

public interface VacancyDAO extends CommonDAO<Vacancy, Long>{
    List<Vacancy> findByCriteria(String vacancyName, Long companyId, BigDecimal minSalary, EducationalInstitution education);
    List<Vacancy> findTopVacanciesByViews();
    void incrementViews(Long vacancyId);
}
