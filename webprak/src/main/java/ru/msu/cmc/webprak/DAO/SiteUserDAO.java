package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.EducationalInstitution;
import ru.msu.cmc.webprak.models.Role;
import ru.msu.cmc.webprak.models.SiteUser;
import ru.msu.cmc.webprak.models.UserStatus;

import java.util.List;

public interface SiteUserDAO extends CommonDAO<SiteUser, Long>{
    SiteUser findByLogin(String login);
    List<SiteUser> findByCriteria(Role role, UserStatus status, EducationalInstitution education);
    SiteUser getUserByLogin(String login);
}
