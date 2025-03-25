package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Resume;
import java.util.List;
import java.math.BigDecimal;

public interface ResumeDAO extends CommonDAO<Resume, Long>{
    List<Resume> findByCriteria(String resume_name, Long userId, BigDecimal minSalary);
    List<Resume> findTopResumesByViews();
    void incrementViews(Long resumeId);
}
