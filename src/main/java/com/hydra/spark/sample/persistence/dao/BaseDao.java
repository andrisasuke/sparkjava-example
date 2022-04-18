package com.hydra.spark.sample.persistence.dao;

import com.hydra.spark.sample.persistence.domain.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public abstract class BaseDao<P extends Base> {

    private static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

    public P findById(Class<P> clazz, Integer id) {
        try {
            return entityManager().find(clazz, id);
        } catch (Exception e) {
            logger.error("failed to find entity id {} :{}", id, e.getMessage());
            return null;
        }
    }

    protected abstract EntityManager entityManager();

}
