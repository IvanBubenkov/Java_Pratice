package ru.msu.cmc.webprak.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.RoleDAO;
import ru.msu.cmc.webprak.models.Role;

import java.util.List;

@Repository
public class RoleDAOImpl extends CommonDAOImpl<Role, Long> implements RoleDAO {
    public RoleDAOImpl() {
        super(Role.class);
    }
    @Override
    public Role getRoleByName(String roleName) {
        try (Session session = sessionFactory.openSession()) {
            // Создание CriteriaBuilder и CriteriaQuery
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Role> query = builder.createQuery(Role.class);
            Root<Role> root = query.from(Role.class);

            // Условие поиска по имени роли
            Predicate condition = builder.equal(root.get("roleName"), roleName);
            query.select(root).where(condition);

            // Выполнение запроса
            Query<Role> resultQuery = session.createQuery(query);
            // Получение списка результатов
            List<Role> roles = resultQuery.getResultList();

            // Если найдено ровно одно совпадение, возвращаем его, иначе null
            return roles.size() == 1 ? roles.get(0) : null;
        }
    }
}
