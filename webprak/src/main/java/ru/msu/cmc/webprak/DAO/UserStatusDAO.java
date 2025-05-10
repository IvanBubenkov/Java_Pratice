package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.UserStatus;

import java.util.List;

public interface UserStatusDAO extends CommonDAO<UserStatus, Long>{
    UserStatus getUserStatusByName(String statusName);
    List<UserStatus> getUserStatuses();
    List<UserStatus> getCompanyStatus();
}
