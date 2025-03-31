package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.UserStatus;

public interface UserStatusDAO extends CommonDAO<UserStatus, Long>{
    UserStatus getUserStatusByName(String statusName);
}
