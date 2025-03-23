package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Educational_institutions;
import ru.msu.cmc.webprak.models.Vacancy;
import java.util.List;
import java.math.BigDecimal;

public interface VacancyDAO extends CommonDAO<Vacancy, Long>{
    List<Vacancy> findByCriteria(String vacancyName, Long companyId, BigDecimal minSalary, Educational_institutions education);
    List<Vacancy> findTopVacanciesByViews();
}
