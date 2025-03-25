package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.ResumeDAO;
import ru.msu.cmc.webprak.models.Resume;

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
    public List<Resume> findByCriteria(String resume_name, Long userId, BigDecimal minSalary) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Resume> query = builder.createQuery(Resume.class);
            Root<Resume> root = query.from(Resume.class);

            List<Predicate> predicates = new ArrayList<>();

            if (resume_name != null) {
                predicates.add(builder.like(root.get("resume_name"), likeExpr(resume_name)));
            }

            if (userId != null) {
                predicates.add(builder.equal(root.get("user_id").get("id"), userId));
            }

            if (minSalary != null) {
                predicates.add(builder.ge(root.get("min_salary_req"), minSalary));
            }

            if (!predicates.isEmpty()) {
                query.where(predicates.toArray(new Predicate[0]));
            }

            return session.createQuery(query).getResultList();
        }
    }

    @Override
    public Long getNumberOfViews(Long resumeId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT number_of_views FROM Resume WHERE id = :resumeId", Long.class)
                    .setParameter("resumeId", resumeId)
                    .uniqueResult();
        }
    }

    @Override
    public void setNumberOfViews(Long resumeId, Long newNumberOfViews) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Обновляем количество просмотров в базе данных
            session.createQuery(
                            "UPDATE Resume SET number_of_views = :newNumberOfViews WHERE id = :resumeId")
                    .setParameter("newNumberOfViews", newNumberOfViews)
                    .setParameter("resumeId", resumeId)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Override
    public List<Resume> findTopResumesByViews() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Resume> cq = cb.createQuery(Resume.class);
            Root<Resume> root = cq.from(Resume.class);

            cq.orderBy(cb.desc(root.get("number_of_views")));

            return session.createQuery(cq)
                    .setMaxResults(TOP_RESUMES_LIMIT)
                    .getResultList();
        }
    }

    @Override
    public void incrementViews(Long resumeId) {
        Long currentViews = getNumberOfViews(resumeId);
        if (currentViews != null) {
            setNumberOfViews(resumeId, currentViews + 1);
        }
    }

    private String likeExpr(String param) {
        return "%" + param + "%";
    }
}
