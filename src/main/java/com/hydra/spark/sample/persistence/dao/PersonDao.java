package com.hydra.spark.sample.persistence.dao;

import com.google.inject.Inject;
import com.hydra.spark.sample.persistence.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class PersonDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonDao.class);

    @Inject
    EntityManager entityManager;

    @Inject
    EntityTransaction entityTransaction;

    public Person find(Integer id) {

        final EntityManager em = entityManager;
        try {
            return em.find(Person.class, id);
        } catch (RuntimeException e) {
            LOGGER.error("failed to find person id {} :{}",id, e.getMessage());
            return null;
        }
    }

    public Person save(String name, String address, String phone) {
        final EntityManager em = entityManager;
        final EntityTransaction trx = entityTransaction;

        Person person = new Person(name, address, phone);

        try {

            trx.begin();
            em.persist(person);
            trx.commit();
            return person;

        } catch (RuntimeException e) {
            if (trx != null && trx.isActive()) {
                trx.rollback();
            }
            LOGGER.error("failed to persist person {} :{}",name, e.getMessage());
            return null;
        }
    }

}
