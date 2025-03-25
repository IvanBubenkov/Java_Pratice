package ru.msu.cmc.webprak.DAO.impl;

import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.UserStatusDAO;
import ru.msu.cmc.webprak.models.UserStatus;

@Repository
public class UserStatusDAOImpl extends CommonDAOImpl<UserStatus, Long> implements UserStatusDAO {
    public UserStatusDAOImpl() {
        super(UserStatus.class);
    }
}
