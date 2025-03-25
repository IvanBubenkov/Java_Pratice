package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
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
                predicates.add(builder.like(root.get("vacancyName"), likeExpr(vacancyName)));
            }

            // Фильтр по ID компании (точное совпадение)
            if (companyId != null) {
                predicates.add(builder.equal(root.get("company").get("id"), companyId));
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
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Vacancy> query = builder.createQuery(Vacancy.class);
            Root<Vacancy> root = query.from(Vacancy.class);

            query.select(root).orderBy(builder.desc(root.get("numberOfViews")));

            return session.createQuery(query).setMaxResults(TOP_RESUMES_LIMIT).getResultList();
        }
    }

    @Override
    public void incrementViews(Long vacancyId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Загружаем вакансию по ID
            Vacancy vacancy = session.get(Vacancy.class, vacancyId);

            if (vacancy != null) {
                // Инкрементируем количество просмотров
                vacancy.setNumberOfViews(vacancy.getNumberOfViews() + 1);
                session.update(vacancy);  // Обновляем запись
            }

            session.getTransaction().commit();
        }
    }

    private String likeExpr(String param) {
        return "%" + param + "%";
    }
}

