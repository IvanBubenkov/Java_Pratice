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
    public List<Resume> findByCriteria(String resumeName, Long userId, BigDecimal minSalary) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Resume> query = builder.createQuery(Resume.class);
            Root<Resume> root = query.from(Resume.class);

            List<Predicate> predicates = new ArrayList<>();

            if (resumeName != null) {
                predicates.add(builder.like(root.get("resumeName"), likeExpr(resumeName)));
            }

            if (userId != null) {
                predicates.add(builder.equal(root.get("user").get("id"), userId));
            }

            if (minSalary != null) {
                predicates.add(builder.ge(root.get("minSalaryRequirement"), minSalary));
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
        return param == null ? "%" : "%" + param + "%";
    }
}
