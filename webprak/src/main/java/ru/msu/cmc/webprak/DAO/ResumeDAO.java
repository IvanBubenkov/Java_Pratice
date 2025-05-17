package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.EducationalInstitution;
import ru.msu.cmc.webprak.models.Resume;
import java.util.List;
import java.math.BigDecimal;

public interface ResumeDAO extends CommonDAO<Resume, Long>{
    List<Resume> findByCriteria(String resumeName,
                                Long userId,
                                EducationalInstitution education,
                                String previousPosition,
                                BigDecimal minSalary,
                                BigDecimal maxSalary);
    List<Resume> findTopResumesByViews();
    void incrementViews(Long resumeId);
}