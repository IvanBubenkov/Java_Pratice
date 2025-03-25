package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.VacancyDAO;
import ru.msu.cmc.webprak.models.EducationalInstitution;
import ru.msu.cmc.webprak.models.Vacancy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VacancyDAOImpl extends CommonDAOImpl<Vacancy, Long> implements VacancyDAO {

    private static final int TOP_RESUMES_LIMIT = 10;

    public VacancyDAOImpl() {
        super(Vacancy.class);
    }

    @Override
    public List<Vacancy> findByCriteria(String vacancyName, Long companyId, BigDecimal minSalary, EducationalInstitution education) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Vacancy> query = builder.createQuery(Vacancy.class);
            Root<Vacancy> root = query.from(Vacancy.class);

            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по имени вакансии (LIKE %имя%)
            if (vacancyName != null) {
                predicates.add(builder.like(root.get("vacancy_name"), likeExpr(vacancyName)));
            }

            // Фильтр по ID компании (точное совпадение)
            if (companyId != null) {
                predicates.add(builder.equal(root.get("company_id").get("id"), companyId));
            }

            // Фильтр по минимальной зарплате (>= minSalary)
            if (minSalary != null) {
                predicates.add(builder.ge(root.get("minSalary"), minSalary));
            }

            // Фильтр по образованию (точное совпадение)
            if (education != null) {
                predicates.add(builder.equal(root.get("education"), education));
            }

            // Применяем условия к запросу (если есть хотя бы одно)
            if (!predicates.isEmpty()) {
                query.where(predicates.toArray(new Predicate[0]));
            }

            // Выполняем запрос и возвращаем результат
            return session.createQuery(query).getResultList();
        }
    }

    @Override
    public List<Vacancy> findTopVacanciesByViews() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM Vacancy ORDER BY number_of_views DESC", Vacancy.class)
                    .setMaxResults(TOP_RESUMES_LIMIT)  // Ограничиваем количество записей
                    .getResultList();
        }
    }

    @Override
    public void incrementViews(Long vacancyId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery(
                    "UPDATE Vacancy SET number_of_views = number_of_views + 1 WHERE id = :vacancyId");
            query.setParameter("vacancyId", vacancyId);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    private String likeExpr(String param) {
        return "%" + param + "%";
    }
}

