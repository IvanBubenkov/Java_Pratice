package ru.msu.cmc.webprak.DAO;

import ru.msu.cmc.webprak.models.Role;

public interface RoleDAO extends CommonDAO<Role, Long>{
    Role getRoleByName(String roleName);
}
