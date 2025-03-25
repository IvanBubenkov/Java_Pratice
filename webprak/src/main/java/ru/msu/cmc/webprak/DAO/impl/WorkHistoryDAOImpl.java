package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.Work_historyDAO;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.WorkHistory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WorkHistoryDAOImpl extends CommonDAOImpl<WorkHistory, Long> implements Work_historyDAO {

    public WorkHistoryDAOImpl() {
        super(WorkHistory.class);
    }

    @Override
    public List<WorkHistory> findByCriteria(String vacancyName, SiteUser applicant, SiteUser company, Long minSalary, LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<WorkHistory> query = builder.createQuery(WorkHistory.class);
            Root<WorkHistory> root = query.from(WorkHistory.class);

            List<Predicate> predicates = new ArrayList<>();

            // Фильтрация по имени вакансии (с использованием LIKE)
            if (vacancyName != null && !vacancyName.isEmpty()) {
                predicates.add(builder.like(root.get("vacancy_name"), "%" + vacancyName + "%"));
            }

            // Фильтрация по applicant_id (если не null)
            if (applicant != null) {
                predicates.add(builder.equal(root.get("applicant_id"), applicant));
            }

            // Фильтрация по company_id (если не null)
            if (company != null) {
                predicates.add(builder.equal(root.get("company_id"), company));
            }

            // Фильтрация по минимальной зарплате (>= minSalary)
            if (minSalary != null) {
                predicates.add(builder.ge(root.get("salary"), minSalary));
            }

            // Фильтрация по датам (если заданы)
            if (startDate != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("date_start"), startDate));
            }

            if (endDate != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("date_end"), endDate));
            }

            // Применяем все условия фильтрации
            if (!predicates.isEmpty()) {
                query.where(predicates.toArray(new Predicate[0]));
            }

            // Выполняем запрос и возвращаем результат
            return session.createQuery(query).getResultList();
        }
    }
}

