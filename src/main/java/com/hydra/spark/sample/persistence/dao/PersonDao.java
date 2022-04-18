package com.hydra.spark.sample.persistence.dao;

import com.google.inject.Inject;
import com.hydra.spark.sample.persistence.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.List;

public class PersonDao extends BaseDao<Person> {

    private static final Logger logger = LoggerFactory.getLogger(PersonDao.class);

    @Inject
    EntityManager entityManager;

    public Person find(Integer id) {
        return findById(Person.class, id);
    }

    public Person save(String name, String address, String phone) {
        final EntityTransaction trx = entityManager.getTransaction();

        Person person = new Person(name, address, phone);
        try {
            trx.begin();
            entityManager.persist(person);
            trx.commit();
            return person;
        } catch (Exception e) {
            if (trx != null && trx.isActive()) {
                trx.rollback();
            }
            logger.error("failed to persist person {} :{}",name, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Person> findName(String name) {
        if(!StringUtils.isEmpty(name)){
            name = name + "%";
        } else {
            return new ArrayList<>();
        }

        try {
            Query query = entityManager.createQuery("SELECT p FROM Person p " +
                    "WHERE LOWER(p.name) LIKE LOWER(:NAME)");
            query.setParameter("NAME", name);
            return (List<Person>) query.getResultList();
        } catch (Exception e) {
            logger.error("failed to find person name {} :{}",name, e.getMessage());
            return null;
        }
    }

    @Override
    protected EntityManager entityManager() {
        return this.entityManager;
    }

}
