package ru.msu.cmc.webprak.DAO.impl;

import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.User_statusesDAO;
import ru.msu.cmc.webprak.models.User_statuses;

@Repository
public class User_statusesDAOImpl extends CommonDAOImpl<User_statuses, Long> implements User_statusesDAO {
    public User_statusesDAOImpl() {
        super(User_statuses.class);
    }
}
