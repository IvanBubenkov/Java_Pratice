package ru.msu.cmc.webprak.DAO.impl;

import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.RolesDAO;
import ru.msu.cmc.webprak.models.Roles;

@Repository
public class RolesDAOImpl extends CommonDAOImpl<Roles, Long> implements RolesDAO {
    public RolesDAOImpl() {
        super(Roles.class);
    }
}
