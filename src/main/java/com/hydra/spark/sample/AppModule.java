package com.hydra.spark.sample;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hydra.spark.sample.controller.PersonController;
import com.hydra.spark.sample.persistence.dao.PersonDao;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class AppModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppModule.class);

    @Override
    public void configure(Binder binder) {
        // bind your all singleton classes (DAO, services, controller, etc)
        binder.bind(PersonDao.class).asEagerSingleton();
        binder.bind(PersonController.class).asEagerSingleton();
    }

    @Singleton
    @Provides
    public EntityManager providesEntityManager() {
        LOGGER.info("Initialize entity manager from persistence");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        return em;
    }

    @Singleton
    @Provides
    public Config providesConfigFactory(){
        Config config = ConfigFactory.load("application");
        return config;
    }

}
