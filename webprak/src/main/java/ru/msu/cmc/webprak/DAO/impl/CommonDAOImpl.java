package ru.msu.cmc.webprak.DAO.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.webprak.DAO.CommonDAO;
import ru.msu.cmc.webprak.models.CommonEntity;

import jakarta.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.Collection;

@Repository
public abstract class CommonDAOImpl<T extends CommonEntity<ID>, ID extends Serializable> implements CommonDAO<T, ID> {

    protected SessionFactory sessionFactory;
    protected Class<T> persistentClass;

    public CommonDAOImpl(Class<T> entityClass) {
        this.persistentClass = entityClass;
    }

    @Autowired
    public void setSessionFactory(LocalSessionFactoryBean sessionFactory) {
        this.sessionFactory = sessionFactory.getObject();
    }

    @Transactional
    @Override
    public T getById(ID id) {
        return sessionFactory.getCurrentSession().get(persistentClass, id);
    }

    @Transactional
    @Override
    public Collection<T> getAll() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(persistentClass);
        criteriaQuery.from(persistentClass);
        return session.createQuery(criteriaQuery).getResultList();
    }

    @Transactional
    @Override
    public void save(T entity) {
        if (entity.getId() == null) {
            // Если ID не установлен, то это новый объект, сохраняем его
            sessionFactory.getCurrentSession().save(entity);
        } else {
            // Если ID уже существует, то это обновление существующего объекта
            sessionFactory.getCurrentSession().update(entity);
        }
    }

    @Transactional
    @Override
    public void saveCollection(Collection<T> entities) {
        Session session = sessionFactory.getCurrentSession();
        for (T entity : entities) {
            this.save(entity);
        }
    }

    @Transactional
    @Override
    public void update(T entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Transactional
    @Override
    public void delete(T entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Transactional
    @Override
    public void deleteById(ID id) {
        T entity = getById(id);
        sessionFactory.getCurrentSession().delete(entity);
    }
}
