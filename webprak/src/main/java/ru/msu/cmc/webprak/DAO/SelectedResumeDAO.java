package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Resume;
import ru.msu.cmc.webprak.models.SelectedResume;
import ru.msu.cmc.webprak.models.SiteUser;

import java.util.List;

public interface SelectedResumeDAO extends CommonDAO<SelectedResume, Long> {
    List<SelectedResume> findByCompanyId(Long companyId);
    SelectedResume findByResumeAndCompany(Resume resume, SiteUser company);
    boolean existsByResumeAndCompany(Resume resume, SiteUser company);
}