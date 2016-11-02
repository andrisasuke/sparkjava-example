package com.hydra.spark.sample.persistence.dao;

import com.google.inject.Inject;
import com.hydra.spark.sample.persistence.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public class PersonDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonDao.class);

    @Inject
    EntityManager entityManager;

    public Person find(Integer id) {

        final EntityManager em = entityManager;
        try {
            return em.find(Person.class, id);
        } catch (RuntimeException e) {
            LOGGER.error("failed to find person id {} :{}",id, e.getMessage());
            return null;
        }
    }

}
