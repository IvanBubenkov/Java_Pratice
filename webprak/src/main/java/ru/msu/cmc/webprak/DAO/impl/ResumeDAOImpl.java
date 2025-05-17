package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.ResumeDAO;
import ru.msu.cmc.webprak.models.EducationalInstitution;
import ru.msu.cmc.webprak.models.Resume;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.WorkHistory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ResumeDAOImpl extends CommonDAOImpl<Resume, Long> implements ResumeDAO {

    private static final int TOP_RESUMES_LIMIT = 10;

    public ResumeDAOImpl() {
        super(Resume.class);
    }

    @Override
    public List<Resume> findByCriteria(String resumeName,
                                       Long userId,
                                       EducationalInstitution education,
                                       String previousPosition,
                                       BigDecimal minSalary,
                                       BigDecimal maxSalary) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Resume> query = builder.createQuery(Resume.class);
            Root<Resume> root = query.from(Resume.class);

            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по названию резюме
            if (resumeName != null && !resumeName.isEmpty()) {
                predicates.add(builder.like(
                        builder.lower(root.get("resumeName")),
                        likeExpr(resumeName.toLowerCase())
                ));
            }

            // Фильтр по пользователю
            if (userId != null) {
                predicates.add(builder.equal(root.get("user").get("id"), userId));
            }

            // Фильтр по образованию
            if (education != null) {
                predicates.add(builder.equal(
                        root.get("user").get("education").get("id"),
                        education.getId()
                ));
            }

            // Фильтр по предыдущей должности (через WorkHistory)
            if (previousPosition != null && !previousPosition.isEmpty()) {
                Join<Resume, SiteUser> userJoin = root.join("user");
                Join<SiteUser, WorkHistory> workHistoryJoin = userJoin.join("workHistories");

                predicates.add(builder.like(
                        builder.lower(workHistoryJoin.get("vacancyName")),
                        likeExpr(previousPosition.toLowerCase())
                ));
            }

            // Фильтр по зарплате
            if (minSalary != null) {
                predicates.add(builder.ge(root.get("minSalaryRequirement"), minSalary));
            }
            if (maxSalary != null) {
                predicates.add(builder.le(root.get("minSalaryRequirement"), maxSalary));
            }

            // Применяем все условия
            if (!predicates.isEmpty()) {
                query.where(predicates.toArray(new Predicate[0]));
            }

            // Сортировка по количеству просмотров
            query.orderBy(builder.desc(root.get("numberOfViews")));

            return session.createQuery(query).getResultList();
        }
    }

    @Override
    public List<Resume> findTopResumesByViews() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Resume> cq = cb.createQuery(Resume.class);
            Root<Resume> root = cq.from(Resume.class);

            cq.orderBy(cb.desc(root.get("numberOfViews")));

            return session.createQuery(cq)
                    .setMaxResults(TOP_RESUMES_LIMIT)
                    .getResultList();
        }
    }

    @Override
    public void incrementViews(Long resumeId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Resume resume = session.get(Resume.class, resumeId);
            if (resume != null) {
                resume.setNumberOfViews(resume.getNumberOfViews() + 1);
                session.merge(resume);
            }
            session.getTransaction().commit();
        }
    }

    private String likeExpr(String param) {
        return "%" + (param == null ? "" : param) + "%";
    }
}