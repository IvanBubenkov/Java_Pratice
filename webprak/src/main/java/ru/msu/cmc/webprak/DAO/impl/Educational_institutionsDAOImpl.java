package ru.msu.cmc.webprak.DAO.impl;

import org.springframework.stereotype.Repository;
import ru.msu.cmc.webprak.DAO.Educational_institutionsDAO;
import ru.msu.cmc.webprak.models.Educational_institutions;

@Repository
public class Educational_institutionsDAOImpl extends CommonDAOImpl<Educational_institutions, Long> implements Educational_institutionsDAO {
    public Educational_institutionsDAOImpl() {
        super(Educational_institutions.class);
    }
}
