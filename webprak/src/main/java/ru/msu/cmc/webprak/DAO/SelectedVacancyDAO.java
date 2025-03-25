package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.SelectedVacancy;
import java.util.List;

public interface SelectedVacancyDAO extends CommonDAO<SelectedVacancy, Long>{
    List<SelectedVacancy> findByApplicantId(Long applicantId);
}
