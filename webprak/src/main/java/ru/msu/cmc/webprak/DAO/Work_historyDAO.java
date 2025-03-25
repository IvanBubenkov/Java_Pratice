package ru.msu.cmc.webprak.DAO;

import java.time.LocalDate;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.WorkHistory;
import java.util.List;

public interface Work_historyDAO extends CommonDAO<WorkHistory, Long> {
    List<WorkHistory> findByCriteria(String vacancyName, SiteUser applicant, SiteUser company, Long minSalary, LocalDate startDate, LocalDate endDate);
}
