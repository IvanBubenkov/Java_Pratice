package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
    public List<Resume> findTopResumesByViews() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM Resume ORDER BY number_of_views DESC", Resume.class)
                    .setMaxResults(TOP_RESUMES_LIMIT)
                    .getResultList();
        }
    }

    @Override
    public void incrementViews(Long resumeId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery(
                    "UPDATE Resume SET number_of_views = number_of_views + 1 WHERE id = :resumeId");
            query.setParameter("resumeId", resumeId);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    private String likeExpr(String param) {
        return "%" + param + "%";
    }
}
