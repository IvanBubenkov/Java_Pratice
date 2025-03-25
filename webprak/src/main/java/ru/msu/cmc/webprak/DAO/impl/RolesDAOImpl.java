package ru.msu.cmc.webprak.DAO.impl;

import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.RoleDAO;
import ru.msu.cmc.webprak.models.Role;

@Repository
public class RolesDAOImpl extends CommonDAOImpl<Role, Long> implements RoleDAO {
    public RolesDAOImpl() {
        super(Role.class);
    }
}
