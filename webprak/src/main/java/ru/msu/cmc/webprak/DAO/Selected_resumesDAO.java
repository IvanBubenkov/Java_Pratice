package ru.msu.cmc.webprak.DAO;


import ru.msu.cmc.webprak.models.Selected_resumes;
import java.util.List;

public interface Selected_resumesDAO extends CommonDAO<Selected_resumes, Long>{
    List<Selected_resumes> findByCompanyId(Long companyId);
}
