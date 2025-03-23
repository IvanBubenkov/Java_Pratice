package ru.msu.cmc.webprak.DAO;

import java.time.LocalDate;
import ru.msu.cmc.webprak.models.Site_user;
import ru.msu.cmc.webprak.models.Work_history;
import java.util.List;

public interface Work_historyDAO extends CommonDAO<Work_history, Long> {
    List<Work_history> findByCriteria(String vacancyName, Site_user applicant, Site_user company, Long minSalary, LocalDate startDate, LocalDate endDate);
}
