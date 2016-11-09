package com.hydra.spark.sample.controller;

import com.google.inject.Inject;
import com.hydra.spark.sample.persistence.dao.PersonDao;
import com.hydra.spark.sample.persistence.domain.Person;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    @Inject
    PersonDao personDao;

    @Inject
    Config config;

    public Person getById(Integer id){
        return personDao.find(id);
    }
    
    public Person add(String name, String address, String phone) {
        return personDao.save(name, address, phone);
    }

    public List<Person> findNameLike(String name){
        return personDao.findName(name);
    }

    public String config() {
        // string config
        Config root = config.getConfig("config");
        String titleString1 = config.getConfig("config").getConfig("notification").getString("title");
        String titleString2 = config.getString("config.notification.title");
        String messageString1 = config.getString("config.notification.message");
        LOGGER.info("root config {}", root);
        LOGGER.info("string title {}", titleString1);
        LOGGER.info("string title {}", titleString2);
        LOGGER.info("string message {}", messageString1);

        // json config
        Config jsonRoot = config.getConfig("foo");
        int jsonInt1 = config.getConfig("foo").getInt("bar");
        int jsonInt2 = config.getInt("foo.bar");
        int jsonInt3 = config.getInt("foo.baz");
        LOGGER.info("root json {}", jsonRoot);
        LOGGER.info("int json {}", jsonInt1);
        LOGGER.info("int bar {}", jsonInt2);
        LOGGER.info("int baz {}", jsonInt3);

        // environment variable
        String envVar = config.getString("os-variable");
        LOGGER.info("env variable {}", envVar);

        return "OK";
    }
}
