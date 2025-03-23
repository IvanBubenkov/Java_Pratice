package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Selected_vacancies;
import java.util.List;

public interface Selected_vacanciesDAO extends CommonDAO<Selected_vacancies, Long>{
    List<Selected_vacancies> findByCompanyId(Long applicantId);
}
