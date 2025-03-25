package ru.msu.cmc.webprak.DAO;


import ru.msu.cmc.webprak.models.SelectedResume;
import java.util.List;

public interface SelectedResumeDAO extends CommonDAO<SelectedResume, Long>{
    List<SelectedResume> findByCompanyId(Long companyId);
}
