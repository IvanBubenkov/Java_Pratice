package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Educational_institutions;
import ru.msu.cmc.webprak.models.Roles;
import ru.msu.cmc.webprak.models.Site_user;
import ru.msu.cmc.webprak.models.User_statuses;

import java.util.List;

public interface Site_userDAO extends CommonDAO<Site_user, Long>{
    Site_user findByLogin(String login);
    List<Site_user> findByRole(Roles role);
    List<Site_user> findByStatus(User_statuses status);
    List<Site_user> findByEducation(Educational_institutions education);
}
